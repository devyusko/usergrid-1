package org.apache.usergrid.event;

import org.apache.usergrid.persistence.collection.CollectionScope;
import org.apache.usergrid.persistence.collection.event.EntityVersionCreated;
import org.apache.usergrid.persistence.model.entity.Entity;

public class EntityVersionCreatedImpl implements EntityVersionCreated {

	@Override
	public void versionCreated(CollectionScope scope, Entity entity) {
		// TODO Auto-generated method stub

	}

}
