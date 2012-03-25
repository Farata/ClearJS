package com.farata.test.events
{
	import com.farata.test.dto.CompanyDTO;
	
	import flash.events.Event;

	public class CompanyEvent extends Event
	{
		public static const COMPANY_SELECTION_CHANGE:String ="companySelectionChange";
		
		public var company:CompanyDTO;
		public function CompanyEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
		
	}
}