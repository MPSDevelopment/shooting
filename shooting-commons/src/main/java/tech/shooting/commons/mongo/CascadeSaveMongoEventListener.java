package tech.shooting.commons.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.util.ReflectionUtils;

//@Component
public class CascadeSaveMongoEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    private MongoOperations mongoOperations;
    
//	@Override
//	public void onBeforeConvert(BeforeConvertEvent<Object> event) {
//		final Object source = event.getSource();
//		if (source instanceof BaseDocument) {
//			if (((BaseDocument) source).getId() == null) {
//				((BaseDocument) source).setId(String.valueOf(IdGenerator.nextId()));
//			}
//		}
//	}

    @Override
    public void onBeforeSave(BeforeSaveEvent<Object> event) {
        final Object source = event.getSource();
//		if (source instanceof BaseDocument) {
//			if (((BaseDocument) source).getId() == null) {
//				((BaseDocument) source).setId(String.valueOf(IdGenerator.nextId()));
//			}
//		}
        ReflectionUtils.doWithFields(source.getClass(), new CascadeCallback(source, mongoOperations));
    }
}