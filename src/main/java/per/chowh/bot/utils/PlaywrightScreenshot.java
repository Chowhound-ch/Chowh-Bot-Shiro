package per.chowh.bot.utils;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

import java.util.function.Function;

public class PlaywrightScreenshot {
    public static byte[] takeScreenshot(String url) {
        return withPage(page -> {
            page.navigate(url);
            page.waitForLoadState(LoadState.NETWORKIDLE);
            return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        });
    }

    public static byte[] takeHtmlScreenshot(String html, String selector) {
        return withPage(page -> {
            page.setContent(html);
            return page.locator(selector).screenshot();
        });
    }

    private static byte[] withPage(Function<Page, byte[]> capture) {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))) {
            return capture.apply(browser.newPage());
        }
    }
}
