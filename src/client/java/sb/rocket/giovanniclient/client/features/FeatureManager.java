package sb.rocket.giovanniclient.client.features;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import sb.rocket.giovanniclient.client.features.autosolvers.AutoExperiments;
import sb.rocket.giovanniclient.client.features.autosolvers.AutoFusion;
import sb.rocket.giovanniclient.client.features.autosolvers.AutoMelody;
import sb.rocket.giovanniclient.client.features.autosolvers.AutoShardsClaim;

import java.util.ArrayList;
import java.util.List;

public class FeatureManager {
    private static final List<AbstractFeature> FEATURES = new ArrayList<>();

    public static void register(AbstractFeature feature) {
        FEATURES.add(feature);
    }

    public static void registerAll() {
        register(new AutoMelody());
        register(new AutoShardsClaim());
        register(new AutoExperiments());
        register(new AutoFusion());

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            for (AbstractFeature f : FEATURES)
                f.onScreenOpen(screen);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (AbstractFeature f : FEATURES)
                f.onTick(client);
        });
    }
}
