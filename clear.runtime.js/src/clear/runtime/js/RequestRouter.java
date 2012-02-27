package clear.runtime.js;

import com.softwarementors.extjs.djn.api.Registry;
import com.softwarementors.extjs.djn.config.GlobalConfiguration;

import com.softwarementors.extjs.djn.router.dispatcher.Dispatcher;

public class RequestRouter extends
		com.softwarementors.extjs.djn.router.RequestRouter {

	private static RequestRouter instance;
	private Registry _registry;
	private Dispatcher _dispatcher;

	public static RequestRouter getRequestRouter() {
		return instance;
	}

	public Registry getRegistry() {
		return _registry;
	}

	public Dispatcher getDispatcher() {
		return _dispatcher;
	}

	public RequestRouter(Registry registry,
			GlobalConfiguration globalConfiguration, Dispatcher dispatcher) {
		super(registry, globalConfiguration, dispatcher);
		_dispatcher = dispatcher;
		_registry = registry;
		instance = this;
	}
}
