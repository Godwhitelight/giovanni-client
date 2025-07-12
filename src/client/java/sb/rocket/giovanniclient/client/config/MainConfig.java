package sb.rocket.giovanniclient.client.config;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.Category;
import sb.rocket.giovanniclient.client.features.autosolvers.AutoSolversConfig;

public class MainConfig extends Config {
    @Override
    public String getTitle() {
        return "§bGiovanni Client";
    }

    @Expose
    @Category(name = "AutoSolvers", desc = "Various auto solvers for GUIs")
    public AutoSolversConfig asc = new AutoSolversConfig();


}