
package com.farata.hibernate_test.dto;

import java.util.List;

import com.farata.dto2extjs.annotations.*;
import com.farata.dto2extjs.annotations.JSOneToMany.SyncType;


@JSClass(kind=JSClassKind.EXT_JS)
public class CompanyDTO  extends com.farata.hibernate_test.dto.gen._CompanyDTO{
    @JSOneToMany(
    		storeType="com.farata.hibernate_test.collections.AssociateCollection",
    		fillArguments="id",
    		sync=SyncType.BATCH
    ) 

    public List<AssociateDTO> associates;

}		
	