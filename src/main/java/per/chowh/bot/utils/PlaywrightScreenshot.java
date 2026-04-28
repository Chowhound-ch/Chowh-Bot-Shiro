package per.chowh.bot.utils;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

public class PlaywrightScreenshot {
    private static final Playwright playwright = Playwright.create();
    
    public static byte[] takeScreenshot(String url) {
        try (Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true))) {
            Page page = browser.newPage();
            page.navigate(url);
            page.waitForLoadState(LoadState.NETWORKIDLE);
            return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        }
    }
}