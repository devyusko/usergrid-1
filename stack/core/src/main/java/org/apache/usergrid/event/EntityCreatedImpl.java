package org.apache.usergrid.event;

import java.util.UUID;

import org.apache.usergrid.persistence.collection.CollectionScope;
import org.apache.usergrid.persistence.collection.event.EntityCreated;
import org.apache.usergrid.persistence.model.entity.Id;

public class EntityCreatedImpl implements EntityCreated {

	@Override
	public void created(CollectionScope scope, Id entityId, UUID version) {
		// TODO Auto-generated method stub

	}

}
