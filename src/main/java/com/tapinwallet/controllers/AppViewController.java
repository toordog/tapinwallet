package com.tapinwallet.controllers;

import com.tapinwallet.data.BaseController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class AppViewController extends BaseController implements AppShellController.HasHost {

    private AppShellController host;

    @FXML WebView webView;
    
    @FXML
    private void initialize() {     

        Platform.runLater(() -> {
            webView.getEngine().setJavaScriptEnabled(true);
            loadModFromDisk(ctx.getSelectedMod().name(), "index.xhtml");
        });
    }

    
    @Override
    public void setHost(AppShellController host) {
        this.host = host;
    }

    @FXML
    private void handleBackHome() {
        if (host != null) host.goHome();
    }

    // -------- core --------

    private void loadModFromDisk(String modName, String entryFile) {
        try {
            Path base = modsBase();
            Path modDir = base.resolve(modName);
            Files.createDirectories(modDir);

            // 1) bootstrap example mod if missing
            ensureModOnDisk(modName, modDir, new String[]{entryFile});

            // 2) ensure tapin-default.css is present in the BASE dir
            ensureDefaultCss(base);

            // 3) patch entry with relative link to ../tapin-default.css
            String cssHref = "../tapin-default.css";
            Path toLoad = patchEntryWithDefaults(modDir, entryFile, cssHref);

            // 4) load patched file
            String url = toLoad.toUri().toString(); // file://...
            webView.getEngine().load(url);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ensureModOnDisk(String modName, Path modDir, String[] files) {
        for (String f : files) {
            Path dst = modDir.resolve(f);
            if (Files.exists(dst)) continue;
            String cp = "/appmods/" + modName + "/" + f;
            try (InputStream in = AppViewController.class.getResourceAsStream(cp)) {
                if (in != null) {
                    Files.copy(in, dst, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
    
    // Base directory: default ~/appmods; override with -Dtapin.mods.dir=/path
    private Path modsBase() {
        String override = System.getProperty("tapin.mods.dir");
        Path base = (override != null && !override.isBlank())
                ? Paths.get(override)
                : Paths.get(System.getProperty("user.home"), "appmods");
        try { Files.createDirectories(base); } catch (Exception ignored) {}
        return base;
    }

    // Ensure tapin-default.css exists ONCE in the base dir
    private void ensureDefaultCss(Path baseDir) {
        Path dst = baseDir.resolve("tapin-default.css");
        if (Files.exists(dst)) return;
        try (InputStream in = AppViewController.class.getResourceAsStream("/defaults/tapin-default.css")) {
            if (in != null) {
                Files.createDirectories(baseDir);
                Files.copy(in, dst, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // Patch entry XHTML to inject viewport + shared cssHref
    private Path patchEntryWithDefaults(Path modDir, String entryFile, String cssHref) {
        try {
            Path src = modDir.resolve(entryFile);
            if (!Files.exists(src)) return src;

            String html = Files.readString(src, StandardCharsets.UTF_8);
            boolean changed = false;

            // ensure there is a <head> ... </head>
            if (!html.matches("(?is).*<head.*?>.*</head>.*")) {
                if (html.contains("<body")) {
                    html = html.replaceFirst("(?is)(<body[^>]*>)", "<head></head>$1");
                } else if (html.contains("</html>")) {
                    html = html.replaceFirst("(?is)</html>", "<head></head></html>");
                } else {
                    html = "<head></head>" + html;
                }
                changed = true;
            }

            // inject <meta viewport>
            if (!html.matches("(?is).*<meta\\s+name=['\"]viewport['\"].*")) {
                String meta = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\" />";
                html = html.replaceFirst("(?is)</head>", meta + "\n</head>");
                changed = true;
            }

            // inject shared tapin-default.css link if absent
            if (!html.matches("(?is).*<link[^>]+href\\s*=\\s*['\"][^'\"]*tapin-default\\.css['\"][^>]*>.*")) {
                String link = "<link rel=\"stylesheet\" href=\"" + cssHref + "\" />";
                html = html.replaceFirst("(?is)</head>", link + "\n</head>");
                changed = true;
            }

            if (!changed) return src;

            String stagedName = entryFile.endsWith(".xhtml")
                    ? entryFile.substring(0, entryFile.length() - ".xhtml".length()) + ".xhtml"
                    : entryFile + ".tapin";
            Path staged = modDir.resolve(stagedName);

            Files.writeString(staged, html, StandardCharsets.UTF_8);
            return staged;

        } catch (Exception ex) {
            ex.printStackTrace();
            return modDir.resolve(entryFile);
        }
    }
}
