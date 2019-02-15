package tech.shooting.mongo;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import com.mpsdevelopment.plasticine.commons.IdGenerator;

/**
 * Just sets id for the baseobject before saving it
 *
 */
// @Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class SaveWithIdMongoEventListener extends AbstractMongoEventListener<Object> {

	@Override
	public void onBeforeConvert(BeforeConvertEvent<Object> event) {
		final Object source = event.getSource();
		if (source instanceof BaseDocument) {
			if (((BaseDocument) source).getId() == null) {
				((BaseDocument) source).setId(IdGenerator.nextId());
			}
		}
	}

	// @Override
	// public void onBeforeSave(BeforeSaveEvent<Object> event) {
	// final Object source = event.getSource();
	// if (source instanceof BaseDocument) {
	// if (((BaseDocument) source).getId() == null) {
	// ((BaseDocument) source).setId(String.valueOf(IdGenerator.nextId()));
	// }
	// }
	// }
}