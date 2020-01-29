package tech.shooting.ipsc.service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.impinj.octane.BitPointers;
import com.impinj.octane.ConnectionCloseEvent;
import com.impinj.octane.ConnectionCloseListener;
import com.impinj.octane.ImpinjReader;
import com.impinj.octane.MemoryBank;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.PcBits;
import com.impinj.octane.SequenceState;
import com.impinj.octane.TagData;
import com.impinj.octane.TagOp;
import com.impinj.octane.TagOpCompleteListener;
import com.impinj.octane.TagOpReport;
import com.impinj.octane.TagOpSequence;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;
import com.impinj.octane.TagWriteOp;
import com.impinj.octane.TargetTag;
import com.impinj.octane.WordPointers;

import lombok.extern.slf4j.Slf4j;
import net.engio.mbassy.listener.Handler;
import tech.shooting.commons.eventbus.EventBus;
import tech.shooting.commons.utils.JacksonUtils;
import tech.shooting.ipsc.event.RunningOnConnectEvent;
import tech.shooting.ipsc.event.RunningOnDisconnectEvent;
import tech.shooting.ipsc.event.TagDetectedEvent;
import tech.shooting.ipsc.event.TagFinishedEvent;
import tech.shooting.ipsc.event.TagImitatorEvent;
import tech.shooting.ipsc.event.TagImitatorOnlyCodesEvent;
import tech.shooting.ipsc.event.TagRestartEvent;
import tech.shooting.ipsc.event.TagUndetectedEvent;
import tech.shooting.ipsc.pojo.Tag;
import tech.shooting.ipsc.pojo.TagEpc;

@Service
@Slf4j
public class TagService {

	private ImpinjReader impinjReader;

	private Map<String, Tag> map = new HashMap<>();

	private boolean connected = false;

	@Autowired
	private SettingsService settingsService;

	private int laps;

	private boolean started;
	
	private boolean rewriteFlag = false;

	private static String currentETCCode;
	private static String newETCCode;

	private static int outstanding = 0;
	private static int opSpecID = 1;
	private static short EPC_OP_ID = 123;
	private static short PC_BITS_OP_ID = 321;

	public TagService() {
		EventBus.subscribe(this);
	}

	public void start() throws OctaneSdkException {

		map = new HashMap<>();
		impinjReader = new ImpinjReader();

		var serverSettings = settingsService.getSettings();

		try {
			// "192.168.31.212"
			impinjReader.connect(serverSettings.getTagServiceIp());
		} catch (OctaneSdkException e) {
			log.error("Cannot start Tag service : %s", e.getMessage());
			return;
		}

		log.info("Reader has been connected");

		var settings = impinjReader.queryDefaultSettings();
		var report = settings.getReport();
		report.setIncludeAntennaPortNumber(true);
		report.setIncludeSeenCount(true);
		report.setIncludeCrc(true);
		report.setIncludeFirstSeenTime(true);
		report.setIncludeLastSeenTime(true);

		impinjReader.applySettings(settings);

		impinjReader.start();

		log.info("Reader has been started");

		connected = true;

		EventBus.publishEvent(new RunningOnConnectEvent());

		impinjReader.setTagOpCompleteListener(new TagOpCompleteListener() {

			@Override
			public void onTagOpComplete(ImpinjReader arg0, TagOpReport report) {
				log.info("Tag report complete %s", report.getResults());
			}
		});

		impinjReader.setTagReportListener(new TagReportListener() {

			@Override
			public void onTagReported(ImpinjReader arg0, TagReport report) {
//					log.info("Tag report %s", report.getTags().stream().map(item-> item.getCrc()).collect(Collectors.toList()));
//					log.info("On tag report %s", report.getTags().stream().map(item-> JacksonUtils.getFullJson(item)).collect(Collectors.joining(", ")));

				var list = new ArrayList<>();

				report.getTags().forEach(item -> {
					list.add(String.valueOf(item.getCrc()));

					Tag tag = new Tag();
					tag.setCode(String.valueOf(item.getCrc()));
					tag.setFirstSeenTime(item.getFirstSeenTime().getLocalDateTime().getTime());
					tag.setLastSeenTime(item.getLastSeenTime().getLocalDateTime().getTime());

					if (map.get(tag.getCode()) == null) {
						map.put(tag.getCode(), tag);
						EventBus.publishEvent(new TagDetectedEvent(tag.getCode()).setTime(tag.getFirstSeenTime()));
					} else {
//						Tag existingTag = map.get(tag.getCode());
//						existingTag.setLastSeenTime(item.getLastSeenTime().getLocalDateTime().getTime());
//						map.put(tag.getCode(), existingTag);
					}
				});

				// remove tag from map if it is not in tagReport
				map.forEach((code, item) -> {
					if (!list.contains(code)) {
						EventBus.publishEvent(new TagUndetectedEvent(code));
						map.remove(code);
					}
				});

				log.info("On tag report %s", report.getTags().stream().map(item -> {
					Tag tag = new Tag();
					tag.setCode(String.valueOf(item.getCrc()));
					tag.setFirstSeenTime(item.getFirstSeenTime().getLocalDateTime().getTime());
					tag.setLastSeenTime(item.getLastSeenTime().getLocalDateTime().getTime());
					return JacksonUtils.getJson(tag);
				}).collect(Collectors.joining(", ")));
			}
		});

		impinjReader.setConnectionCloseListener(new ConnectionCloseListener() {

			@Override
			public void onConnectionClose(ImpinjReader arg0, ConnectionCloseEvent arg1) {
				connected = false;
				log.info("Reader connection has been closed");
				EventBus.publishEvent(new RunningOnDisconnectEvent());
			}
		});
	}
	
	public String getTagIp() {
		return settingsService.getSettings().getTagServiceIp();
	}

	@Handler
	public void handle(TagDetectedEvent event) {

	}

	@Handler
	public void handle(TagRestartEvent event) throws OctaneSdkException {
		log.info("Tag restart event with ip %s", event.getIp());
		stop();
		start();
	}

	public void stop() throws OctaneSdkException {
		if (impinjReader != null) {
			impinjReader.stop();

			connected = false;

			log.info("Reader has been stopped");

			impinjReader.disconnect();

			log.info("Reader has been disconnected");

			EventBus.publishEvent(new RunningOnDisconnectEvent());
		}
	}

	@Handler
	public void handle(TagImitatorEvent event) throws InterruptedException {
		log.info("Tag imitator event started with %s laps %s persons", event.getLaps(), event.getPersons().size());

		if (event.getLaps() == 0) {
			log.error("There is zero laps");
			return;
		}

		EventBus.publishEvent(new TagFinishedEvent(event.getStandardId()));

		IntStream range = IntStream.rangeClosed(0, event.getLaps()).sequential();

		range.forEach(action -> {

			log.info("Another lap");

			event.getPersons().forEach(item -> {

				EventBus.publishEvent(new TagDetectedEvent(item.getRfidCode()).setTime(System.currentTimeMillis()));

				try {
					Thread.sleep(event.getPersonDelay());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});

			try {
				Thread.sleep(event.getLapDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

	}

	@Handler
	public void handle(TagImitatorOnlyCodesEvent event) throws InterruptedException {
		log.info("Tag imitator only codes event started with %s laps %s persons", event.getLaps(), event.getPersons().size());

		if (event.getLaps() == 0) {
			log.error("There is zero laps");
			return;
		}

		EventBus.publishEvent(new TagFinishedEvent(event.getStandardId()));

		IntStream range = IntStream.rangeClosed(0, event.getLaps()).sequential();

		range.forEach(action -> {

			log.info("Another lap %s", action);

			event.getPersons().forEach(item -> {

				EventBus.publishEvent(new TagDetectedEvent(item.getRfidCode()).setOnlyCode(true).setTime(System.currentTimeMillis()));

				try {
					Thread.sleep(event.getPersonDelay());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});

			try {
				Thread.sleep(event.getLapDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});

	}

	public boolean getStatus() {
		return connected;
	}

	public Map<String, Tag> getMap() {
		return map;
	}

	public void clear() {
		map = new HashMap<>();
	}

	public void startSending(int laps) {
		this.laps = laps;
		map = new HashMap<>();
		started = true;
	}

	public void stopSending() {
		started = false;
	}
	
	public String getLocalIp() {
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getLocalHost();
			return inetAddress.getHostAddress();
		} catch (UnknownHostException e) {
			log.error("Cannot get a local ip address");
		}
		return "Cannot get ip address";
	}

	public InetAddress getFirstNonLoopbackAddress() throws SocketException {
		Enumeration en = NetworkInterface.getNetworkInterfaces();
		while (en.hasMoreElements()) {
			NetworkInterface i = (NetworkInterface) en.nextElement();
			for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
				InetAddress addr = (InetAddress) en2.nextElement();
				if (!addr.isLoopbackAddress()) {
					if (addr instanceof Inet4Address) {
						return addr;
					}
				}
			}
		}
		return null;
	}

	public void rewriteETC(com.impinj.octane.Tag tag) {
		if (rewriteFlag && tag.getEpc().toHexString().equalsIgnoreCase(currentETCCode)) {

			log.info("Start Write new ETC");
			rewriteFlag = false;
			log.info(" EPC  String - %s", tag.getEpc().toString());

			log.info(" TID  String - %s", tag.getTid().toString());

			ByteBuffer buffer = ByteBuffer.allocate(2);
			byte[] arr = buffer.putShort(tag.getCrc()).array();
			for (byte b : arr) {
				log.info("byte = %s =->%8s", b, Integer.toBinaryString(b & 0xFF));
			}
			log.info("Straight ->>> Int value = %5s ---- HEX = %s ", ByteBuffer.wrap(arr).getShort(), DatatypeConverter.printHexBinary(arr));
			ArrayUtils.reverse(arr);
			log.info("Reverse  ->>> Int value = %5s ---- HEX = %s ", ByteBuffer.wrap(arr).getShort(), DatatypeConverter.printHexBinary(arr));

			if (tag.isPcBitsPresent()) {
				short pc = tag.getPcBits();
				String currentEpc = tag.getEpc().toHexString();
				try {
					programEpc(currentEpc, pc, newETCCode);
				} catch (Exception e) {
					log.error("Failed To program EPC: " + e.toString());
				}
			}
		}
	}

	public void rewriteEPCRequest(TagEpc tagEpc) {
		log.info("Request on Rewrite EPC");
		newETCCode = null;
		currentETCCode = null;
		if (StringUtils.isBlank(tagEpc.getCurrentEpc()) || StringUtils.isBlank(tagEpc.getNewEpc())) {
			log.error(" EPC codes dta is EMPTY");
			return;
		}
		currentETCCode = tagEpc.getCurrentEpc();
		newETCCode = tagEpc.getNewEpc();
		rewriteFlag = true;
	}

	private void programEpc(String currentEpc, short currentPC, String newEpc) throws Exception {
		if ((currentEpc.length() % 4 != 0) || (newEpc.length() % 4 != 0)) {
			throw new Exception("EPCs must be a multiple of 16- bits: " + currentEpc + "  " + newEpc);
		}
		if (outstanding > 0) {
			return;
		}

		log.info("Programming Tag ");
		log.info(" EPC %s  to %s ", currentEpc, newEpc);

		TagOpSequence seq = new TagOpSequence();
		seq.setOps(new ArrayList<TagOp>());
		seq.setExecutionCount((short) 1); // delete after one time
		seq.setState(SequenceState.Active);
		seq.setId(opSpecID++);

		seq.setTargetTag(new TargetTag());
		seq.getTargetTag().setBitPointer(BitPointers.Epc);
		seq.getTargetTag().setMemoryBank(MemoryBank.Epc);
		seq.getTargetTag().setData(currentEpc);

		TagWriteOp epcWrite = new TagWriteOp();
		epcWrite.Id = EPC_OP_ID;
		epcWrite.setMemoryBank(MemoryBank.Epc);
		epcWrite.setWordPointer(WordPointers.Epc);
		epcWrite.setData(TagData.fromHexString(newEpc));

		// add to the list
		seq.getOps().add(epcWrite);

		// have to program the PC bits if these are not the same
		if (currentEpc.length() != newEpc.length()) {
			// keep other PC bits the same.
			String currentPCString = PcBits.toHexString(currentPC);

			short newPC = PcBits.AdjustPcBits(currentPC, (short) (newEpc.length() / 4));
			String newPCString = PcBits.toHexString(newPC);

			log.info(" PC bits to establish new length: %s  %s ", newPCString, currentPCString);

			TagWriteOp pcWrite = new TagWriteOp();
			pcWrite.Id = PC_BITS_OP_ID;
			pcWrite.setMemoryBank(MemoryBank.Epc);
			pcWrite.setWordPointer(WordPointers.PcBits);

			pcWrite.setData(TagData.fromHexString(newPCString));
			seq.getOps().add(pcWrite);
		}

		outstanding++;
		impinjReader.addOpSequence(seq);
		log.info("Stop rewrite ETC");
	}

}
