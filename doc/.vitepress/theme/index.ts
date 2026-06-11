import DefaultTheme from "vitepress/theme";
import "./style/index.less"; //引入自定义的样式
import DemoVideoPlayer from "./components/DemoVideoPlayer.vue";

export default {
    extends: DefaultTheme,
    enhanceApp({ app }) {
        app.component("DemoVideoPlayer", DemoVideoPlayer);
    },
    // ...DefaultTheme, //或者这样写也可
};
