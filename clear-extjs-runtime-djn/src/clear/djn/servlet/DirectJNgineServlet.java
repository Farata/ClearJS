package clear.djn.servlet;

import clear.djn.router.RequestRouter;

import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;


public class DirectJNgineServlet extends
		com.softwarementors.extjs.djn.servlet.DirectJNgineServlet {	

	private static final long serialVersionUID = 1L;

	protected RequestRouter createRequestRouter(Registry registry, GlobalConfiguration globalConfiguration) {
		    assert registry != null;
		    assert globalConfiguration != null;
		    	
		    return new RequestRouter( registry, globalConfiguration, createDispatcher(globalConfiguration.getDispatcherClass()) );
	}		  
}
