package sb.rocket.giovanniclient.client;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.*;
import org.lwjgl.glfw.GLFW;

public class MySubCategory {
    @Expose
    @ConfigOption(name = "Text Test", desc = "Text Editor Test")
    @ConfigEditorText
    public String text = "Text";

    @Expose
    @Accordion
    @ConfigOption(name = "Hehe", desc = "hoho")
    public MyAccordion myAccordion = new MyAccordion();

    public static class MyAccordion {
        @Expose
        @ConfigOption(name = "Number", desc = "Slider test")
        @ConfigEditorSlider(minValue = 0, maxValue = 10, minStep = 1)
        public int slider = 0;

        @Expose
        @ConfigOption(name = "Key Binding", desc = "Key Binding")
        @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_F)
        public int keyBoard = GLFW.GLFW_KEY_F;
    }
}