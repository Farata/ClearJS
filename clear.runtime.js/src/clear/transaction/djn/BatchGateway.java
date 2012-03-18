package clear.transaction.djn;

import java.util.ArrayList;
import java.util.Map;

import java.util.List;


import com.google.gson.Gson;
import com.softwarementors.extjs.djn.api.RegisteredAction;
import com.softwarementors.extjs.djn.api.RegisteredStandardMethod;
import com.softwarementors.extjs.djn.api.Registry;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import clear.djn.router.RequestRouter;
import clear.transaction.BatchMember;

import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;
import com.softwarementors.extjs.djn.router.processor.RequestException;


public class BatchGateway {


	@DirectMethod
	public List<BatchMember> execute(List<BatchMember> items) {

		RequestRouter router = RequestRouter.getRequestRouter();
		Registry registry = router.getRegistry();
		Dispatcher dispatcher = router.getDispatcher();
		Gson gson = new Gson();
		
		List<BatchMember> results = new ArrayList<BatchMember>(items.size());
		
		for (int i = 0; i < items.size(); i++) {
			@SuppressWarnings("unchecked")
			Map<String, String> mapItem = (Map<String, String>)items.get(i);
			String jsonItem = gson.toJson(mapItem);
			BatchMember batchMember = gson.fromJson(jsonItem, BatchMember.class);
			RegisteredAction action = registry.getAction(batchMember.className); 
			if( action == null ) {
			      throw RequestException.forActionNotFound( batchMember.className );
			}

			RegisteredStandardMethod method = action.getStandardMethod(batchMember.methodName);
			if( method == null ) {
				throw RequestException.forActionMethodNotFound( action.getName(), batchMember.methodName );
			}
		
			batchMember.result = dispatcher.dispatch(method, batchMember.parameters.toArray());
			results.add(batchMember);
		}	
		
		return results;
	}
}
