Ext.define('Test.spec.DataAccess' ,{}, function () {
	
	var store = null, toolbarCtrl = null, companyListCtrl = null, companyCount, company;
 	
	describe("Companies", function() {
        var modifiedCompanyName;

 	    beforeEach(function(){
 	        if (!toolbarCtrl) {
 	        	toolbarCtrl = Application.getController('ToolbarController');
 	        }

	        expect(toolbarCtrl).toBeTruthy();
	        
 	        if (!companyListCtrl) {
 	        	companyListCtrl = Application.getController('CompanyListController');
 	        }
 	        
	        expect(companyListCtrl).toBeTruthy();

 	        if (!store) {
 	        	store = toolbarCtrl.getClearSamplesCompanyStore();
 	        }

 	        expect(store).toBeTruthy();
 	        
 	    });

 	    it("should have companies",function(){
 	        toolbarCtrl.fill();

 	        waitsFor(
 	        	function(){ return !store.isLoading(); },
 	            "fill never completed",
 	            4000
 	        );
 	        
 	        runs( function() { 	        	
	 	    	companyCount = store.getCount();
	 	        expect(companyCount).toBeGreaterThan(1);
 	        });
 	    });
 	    
 	    it("should autoincrement company id during insert",function(){
 	        company = companyListCtrl.insertCompany()[0];
 	        toolbarCtrl.sync();
 	        waitsFor(
 	        	function(){ return !store.commitRequired; },
 	            "sync with insert failed",
 	            10000
 	 	    );
 	        runs (function() { 	        	
 	        	expect(company.getId()).toNotEqual(-1);
 	        });
 	    });
 	    
 	    it("should update company",function(){
 	    	modifiedCompanyName = "Company " + Ext.create('Ext.data.UuidGenerator').generate();
 	        company.set('companyName', modifiedCompanyName);
 	        toolbarCtrl.sync();
 	        waitsFor(
 	        	function(){ return !store.commitRequired; },
 	            "sync with update failed",
 	            10000
 	 	    );
 	        runs( function() { 	        	
 	        	toolbarCtrl.fill();
 	        });
 	        waitsFor(
 	        	function(){ return !store.isLoading(); },
 	            "fill after update never completed",
 	            4000
 	 	    );
 	        runs (function() { 	        	
 	        	expect(company.get('companyName')).toEqual(modifiedCompanyName);
 	        });
 	    });	    

 	    it("should delete company",function(){
 	    	var count = store.getCount(); 	    	
 	        store.removeAt(store.find( 'companyName', modifiedCompanyName, 0, false, false, true ));
 	        toolbarCtrl.sync();
 	        waitsFor(
 	        	function(){ return !store.commitRequired; },
 	            "sync with update failed",
 	            4000
 	 	    );
 	        runs( function() { 	        	
 	        	toolbarCtrl.fill();
 	        });
 	        waitsFor(
 	        	function(){ return !store.isLoading(); },
 	            "fill after delete never completed",
 	            4000
 	 	    );
 	        runs (function() { 	        	
 	        	expect(store.getCount()).toEqual(count-1);
 	        });
 	    });	
/* 	    
 	   it("should insert via associated stores",function(){
 		    var associateStore;
 		    
 		    company = store.getAt(0);
 		    associateStore = company.getAssociates();
 	        waitsFor(
 	        	function(){ return !associateStore.isLoading(); },
 	            "fill never completed",
 	            4000
 	 	    );
 	        runs (function (){
 	        	
 	        });
	    });	
*/	    
/*
 	    it("should open the editor window", function(){
 	        var grid = Ext.ComponentQuery.query('userlist')[0];

 	        ctlr.editUser(grid,store.getAt(0));

 	        var edit = Ext.ComponentQuery.query('useredit')[0];

 	        expect(edit).toBeTruthy();
 	        if(edit)edit.destroy();
 	    });
*/ 	    

	});	
});
