package naturalism.addon.leandick;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class LeanDickAddon extends MeteorAddon {
	public static final Logger LOG = LoggerFactory.getLogger(LeanDickAddon.class);

	@Override
	public void onInitialize() {
		LOG.info("Initializing Meteor Addon Template");

		// Required when using @EventHandler
		MeteorClient.EVENT_BUS.registerLambdaFactory("naturalism.addon.leandick", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
	}

	@Override
	public void onRegisterCategories() {
		//Modules.registerCategory(CATEGORY);
	}
}
