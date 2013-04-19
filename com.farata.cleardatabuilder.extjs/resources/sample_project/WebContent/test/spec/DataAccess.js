Ext.define('Test.spec.DataAccess' ,{}, function () {
	
	var companyStore = null, toolbarCtrl = null, companyCtrl = null;
 	
	describe("Companies", function() {
        var modifiedCompanyName;

        beforeEach(function(){
 	    	var application = this.env.application;
 	        if (!toolbarCtrl) {
 	        	
 	        	toolbarCtrl = application.getController('ToolbarController');
 	        }

	        expect(toolbarCtrl).toBeTruthy();
	        
 	        if (!companyCtrl) {
 	        		companyCtrl = application.getController('CompanyController');
 	        }
 	        
	        expect(companyCtrl).toBeTruthy();

	        if (!companyStore) {
	        		companyStore = toolbarCtrl.getExampleCompanyStore();
 	        }

 	        expect(companyStore).toBeTruthy();
 	        
 	    });

	    it("should persist new company record",function(){
		    	var count = companyStore.getCount();

		    	companyCtrl.insertCompany();
 	        toolbarCtrl.sync();
 	        waitsFor(
 	        		function(){ return !companyStore.commitRequired; },
 	            "sync with insert failed",
 	            10000
 	 	    );
 	        runs (function() { 	        	
 	        		expect(companyStore.getCount() - count).toEqual(1);
 	        });
 	    }); 	    
 	    
 	    it("should persist change of the company name",function(){
	 	    	var company = companyStore.getAt(companyStore.getCount()-1);
	 	    	
 	    		modifiedCompanyName = "Company " + Ext.create('Ext.data.UuidGenerator').generate();
 	        company.set('companyName', modifiedCompanyName);
 	        toolbarCtrl.sync();
 	        waitsFor(
 	        	function(){ return !companyStore.commitRequired; },
 	            "sync with update failed",
 	            10000
 	 	    );
 	        runs( function() { 	        	
 	        		toolbarCtrl.fill();
 	        });
 	        waitsFor(
 	        		function(){ return !companyStore.isLoading(); },
 	            "fill after update never completed",
 	            4000
 	 	    );
 	        runs (function() { 	        	
 	        		expect(company.get('companyName')).toEqual(modifiedCompanyName);
 	        });
 	    });	

 	    it("should persist deletion of a company", function(){
 	    		var count = companyStore.getCount(); 
 	        companyStore.removeAt(count - 1); // the last company is the one that we modified
 	        toolbarCtrl.sync();
 	        waitsFor(
 	        	function(){ return !companyStore.commitRequired; },
 	            "sync with update failed",
 	            4000
 	 	    );
 	        runs( function() { 	        	
 	        	toolbarCtrl.fill();
 	        });
 	        waitsFor(function(){ return !companyStore.isLoading(); },
 	            "fill after delete never completed",
 	            4000
 	 	    );
 	        runs (function() { 	        	
 	        		expect(companyStore.getCount()).toEqual(count-1);
 	        });
 	    });	
 	   
 	    it("should autoincrement company id during commit with new company",function(){
 	    		var newCompany;
 	    		
 	        companyCtrl.insertCompany();
 	        newCompany = companyStore.getAt(companyStore.getCount()-1);
 	        toolbarCtrl.sync();
 	        waitsFor(
 	        		function(){ return !companyStore.commitRequired; },
 	            "sync with insert failed",
 	            10000
 	 	    );
 	        runs (function() { 	        	
 	        		expect(newCompany.getId()).toBeGreaterThan(0);
 	        });
 	    });
 	    
	    it("should default foreign key in the new associate to id of the parent company",function(){
	    		companyCtrl.insertCompany();
			toolbarCtrl.sync();
			waitsFor(
				function(){ return !companyStore.commitRequired; },
				"sync with insert failed",
				10000
			);  
			var company = companyStore.getAt(companyStore.getCount()-1);
	    		var associateStore = company.getAssociates();
 	        waitsFor(
 	        	function(){return !associateStore.isLoading(); },
 	            "get associates never completed",
 	            4000
 	 	    );
 	        runs (function() { 	        	
 	           var associate = associateStore.createModel({
 	                associateName: 'Vasiliy Lokhankin'
 	            });
	 	        associateStore.add(associate);
	 	        	expect(company.getId()===associate.companyId);
 	        });
 	    });
	    
	    it("should propagate autoincremented parent id to new children during new parent/new children commit",function(){
			companyCtrl.insertCompany();
			var company = companyStore.getAt(companyStore.getCount()-1);
    		var associateStore = company.getAssociates();
	        waitsFor(
	        		function(){return !associateStore.isLoading(); },
	            "get associates never completed",
	            4000
	 	    );
	        runs (function() { 	        	
	            var associate = associateStore.createModel({
	                associateName: 'Associate1'
	            });
	 	        associateStore.add(associate);
	            associate = associateStore.createModel({
	                associateName: 'Associate2'
	            });
	 	        associateStore.add(associate);
	 	        toolbarCtrl.sync();
	 	        waitsFor(
	 	        		function(){ return !associateStore.commitRequired; },
	 	        		"sync with insert failed",
	 	        		10000
	 	        ); 
	 	        runs (function() { 	 	        	
	 	        		expect(expect(associate.get("companyId")).toEqual(company.getId()));
	 	        });
	        });
	    });	    
	    
	    it("should autoincrement id in the new associate during new parent/new child commit",function(){
    			companyCtrl.insertCompany();
			var company = companyStore.getAt(companyStore.getCount()-1);
	    		var associateStore = company.getAssociates();
 	        waitsFor(
 	        		function(){return !associateStore.isLoading(); },
 	            "get associates never completed",
 	            4000
 	 	    );
 	        runs (function() { 	        	
 	           var associate = associateStore.createModel({
 	                associateName: 'Vasiliy Lokhankin'
 	            });
	 	        associateStore.add(associate);
	 	        toolbarCtrl.sync();
	 	        waitsFor(
	 	        		function(){ return !associateStore.commitRequired; },
	 	        		"sync with insert failed",
	 	        		10000
	 	        ); 
	 	        runs (function() { 	 	        	
	 	        		expect(expect(associate.getId()).toBeGreaterThan(0));
	 	        });
 	        });
	    });
	});	
});
