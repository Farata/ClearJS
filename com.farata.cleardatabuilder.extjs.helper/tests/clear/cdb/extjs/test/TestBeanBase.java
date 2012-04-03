package clear.cdb.extjs.test;

import javax.persistence.AssociationOverride;

import com.farata.dto2extjs.annotations.JSOneToMany;

public class TestBeanBase {
	
	private String name;

	@JSOneToMany(foreignKey = "someKey", autoLoad=true, getter="getSome", primaryKey="id")
	@AssociationOverride(name = "someName")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
