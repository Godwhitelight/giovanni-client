package sb.rocket.giovanniclient.client;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.Config;
import io.github.notenoughupdates.moulconfig.annotations.Category;

public class TestConfig extends Config {
    @Override
    public String getTitle() {
        return "§bMyMod Config";
    }

    @Expose
    @Category(name = "Category Name", desc = "Category Description")
    public MyCategory myCategory = new MyCategory();

    public static class MyCategory {
        @Expose
        @Category(name = "SubCategory", desc = "Sub category description")
        public MySubCategory subCategory = new MySubCategory();
    }
}