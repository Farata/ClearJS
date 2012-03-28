package clear.djn.servlet;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.api.RegisteredAction;
import com.softwarementors.extjs.djn.api.RegisteredApi;
import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;
import com.softwarementors.extjs.djn.router.dispatcher.DispatcherBase;
import com.softwarementors.extjs.djn.servlet.ssm.SsmDispatcher;

/**
 * Dispatcher used to retrieve DirectAction
 * 
 * @author vlagorce
 * 
 */
public class SpringDispatcher extends SsmDispatcher {

	@Override
	protected Object getActionInstance(RegisteredAction arg0) {
		// TODO Auto-generated method stub
		return null;
	}

    
}
