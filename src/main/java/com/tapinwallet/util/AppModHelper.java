package com.tapinwallet.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tapinwallet.data.ModEntry;
import com.tapinwallet.controllers.AppModViewController;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;

/**
 *
 * @author michael
 */
public class AppModHelper {

    private static String APPMOD_DEFAULT = "appmod-default.css";

    public static Image getImage(ModEntry mod) {
        Path base = modsBase().resolve(mod.hash()+"/"+mod.icon());
        
        if(!base.toFile().exists())
        return null;
        
        return new Image(base.toUri().toString());
    }
    
    // -------- core --------
    public static List<ModEntry> listAvailableModsWithEntries() {
        List<ModEntry> mods = new ArrayList<>();
        Path base = modsBase();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(base)) {
            for (Path dir : ds) {
                if (!Files.isDirectory(dir)) {
                    continue;
                }

                String entry = "index.xhtml";
                String pkg = dir.toString() + "/package.json";

                String jsonContent = Files.readString(dir.resolve(pkg));
                
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonContent);

                String modName = root.get("name").asText();
                String id = root.get("id").asText();
                String icon = root.get("icon").asText();
                mods.add(new ModEntry(id, modName, icon, entry, dir.getFileName().toString()));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mods;
    }

    public static String loadModFromDisk(String modName, String entryFile) {

        try {
            Path base = modsBase();
            Path modDir = base.resolve(modName);

            // 3) patch entry with relative link to ../tapin-default.css
            String cssHref = "../" + APPMOD_DEFAULT;
            Path toLoad = patchEntryWithDefaults(modDir, entryFile, cssHref);

            // 4) load patched file
            String url = toLoad.toUri().toString(); // file://...

            return url;

        } catch (Exception e) {
            return null;
        }
    }

    public static String loadModFromResources(String modName, String entryFile) {
        try {

            Path base = modsBase();
            Path modDir = base.resolve(CryptLite.sha256(modName));

            // 1) bootstrap example mod if missing
            ensureModOnDisk(modName, modDir);

            // 2) ensure tapin-default.css is present in the BASE dir
            ensureDefaultCss(base);

            // 3) patch entry with relative link to ../tapin-default.css
            String cssHref = "../" + APPMOD_DEFAULT;
            Path toLoad = patchEntryWithDefaults(modDir, entryFile, cssHref);

            // 4) load patched file
            String url = toLoad.toUri().toString(); // file://...

            return url;

        } catch (Exception e) {
            return null;
        }
    }

    private static void ensureModOnDisk(String modName, Path modDir) {
        String cp = "appmods/" + modName;

        if (modDir.toFile().exists()) {
            return;
        }

        try {
            copyResourceDir(cp, modDir);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // Ensure tapin-default.css exists ONCE in the base dir
    private static void ensureDefaultCss(Path baseDir) {
        Path dst = baseDir.resolve(APPMOD_DEFAULT);
        if (Files.exists(dst)) {
            return;
        }
        try (InputStream in = AppModViewController.class.getResourceAsStream("/defaults/" + APPMOD_DEFAULT)) {
            if (in != null) {
                Files.createDirectories(baseDir);
                Files.copy(in, dst, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Patch entry XHTML to inject viewport + shared cssHref
    private static Path patchEntryWithDefaults(Path modDir, String entryFile, String cssHref) {

        try {
            Path src = modDir.resolve(entryFile);

            if (!Files.exists(src)) {
                return src;
            }

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
            if (!html.matches("(?is).*<link[^>]+href\\s*=\\s*['\"][^'\"]*" + APPMOD_DEFAULT.replace(".", "\\.") + "['\"][^>]*>.*")) {
                String link = "<link rel=\"stylesheet\" href=\"" + cssHref + "\" />";
                html = html.replaceFirst("(?is)</head>", link + "\n</head>");
                changed = true;
            }

            if (!changed) {
                return src;
            }

            String stagedName = entryFile.endsWith(".html")
                    ? entryFile.substring(0, entryFile.length() - ".html".length()) + ".html"
                    : entryFile + ".tapin";
            Path staged = modDir.resolve(stagedName);

            Files.writeString(staged, html, StandardCharsets.UTF_8);
            return staged;

        } catch (Exception ex) {
            ex.printStackTrace();
            return modDir.resolve(entryFile);
        }
    }

    private static void copyResourceDir(String resourceDir, Path targetDir) throws IOException {

        URL url = AppModHelper.class.getResource("/" + resourceDir);
        if (url == null) {
            throw new IOException("Resource directory not found: " + resourceDir);
        }

        // Running from classes folder or Android native image (GraalVM)
        // We cannot walk the filesystem here; resources are embedded.
        String[] knownFiles = new String[]{
            "index.html",
            "style.css",
            "app.js",
            "icon.png",
            "package.json"
        };

        for (String fileName : knownFiles) {
            String resourcePath = "/" + resourceDir + "/" + fileName;
            try (InputStream in = AppModHelper.class.getResourceAsStream(resourcePath)) {
                if (in == null) {
                    continue; // skip if missing
                }

                // XXX : hash the path name
                Path outFile = targetDir.resolve(fileName);

                if (!outFile.getParent().toFile().exists()) {
                    Files.createDirectories(outFile.getParent());
                }
                try (OutputStream out = Files.newOutputStream(outFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                    in.transferTo(out);
                }
            }
        }
    }

    // Base directory: default ~/appmods; override with -Dtapin.mods.dir=/path
    public static Path modsBase() {
        String override = System.getProperty("tapin.mods.dir");

        Path base = (override != null && !override.isBlank())
                ? Paths.get(override)
                : Paths.get(System.getProperty("user.home"), "appmods");
        try {
            Files.createDirectories(base);
        } catch (Exception ignored) {
        }
        return base;
    }

}
