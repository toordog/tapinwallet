(function () {
    "use strict";

    // --- tiny DOM helpers
    var $ = function (id) {
        return document.getElementById(id);
    };

    // --- demo "does something": counts clicks, timestamps them, and stores in localStorage
    var KEY = "tapin_demo_clicks";

    function readClicks() {
        try {
            return JSON.parse(localStorage.getItem(KEY) || "[]");
        } catch (e) {
            return [];
        }
    }
    function writeClicks(arr) {
        localStorage.setItem(KEY, JSON.stringify(arr));
    }

    var i = 1;

    function runDemo() {
        // window.tapin.encrypt("Michael", function (data,i) {
            var data = Date.now();
            var clicks = readClicks();
            clicks.unshift({at: new Date().toISOString()});
            writeClicks(clicks);

            var out = $("output");

            tapin.getDID(function(did) {
                out.textContent = "Demo ran " + clicks.length + " time(s). Last: " + clicks[0].at + " : " + data + " : "+i+" DID:"+did;
            });

            out.className = "output pulse";

            setTimeout(function () {
                out.className = "output";
                i++;
            }, 500);

        // });
    }

    function setYear() {
        var y = $("year");
        if (y)
            y.textContent = String((new Date()).getFullYear());
    }

    // boot
    function init() {
        setYear();
        var btn = $("demoBtn");
        if (btn) {
            btn.onclick = runDemo;
        }

    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init, false);
    } else {
        init();
    }


})();

async function getInfo() {
    const table = document.getElementById("infoTable");
    
    const addRow = (key, value) => {
        const row = document.createElement("tr");
        row.innerHTML = `<td>${key}</td><td>${value ?? "N/A"}</td>`;
        table.appendChild(row);
    };
    const addCategory = (name) => {
        const row = document.createElement("tr");
        row.innerHTML = `<td colspan="2" class="category">${name}</td>`;
        table.appendChild(row);
    };

    // Browser Info
    addCategory("Browser");
    addRow("User Agent", navigator.userAgent);
    addRow("Browser Language", navigator.language);
    addRow("Cookies Enabled", navigator.cookieEnabled);
    addRow("Online", navigator.onLine);
    addRow("Platform", navigator.platform);
    addRow("Vendor", navigator.vendor);
    addRow("Product", navigator.product);
    addRow("Do Not Track", navigator.doNotTrack);
    addRow("Java Enabled", navigator.javaEnabled?.() ? "Yes" : "No");

    // Screen Info
    addCategory("Screen");
    addRow("Screen Width", screen.width + " px");
    addRow("Screen Height", screen.height + " px");
    addRow("Available Width", screen.availWidth + " px");
    addRow("Available Height", screen.availHeight + " px");
    addRow("Color Depth", screen.colorDepth);
    addRow("Pixel Ratio", window.devicePixelRatio);

    // Window Info
    addCategory("Window");
    addRow("Inner Width", window.innerWidth + " px");
    addRow("Inner Height", window.innerHeight + " px");
    addRow("Outer Width", window.outerWidth + " px");
    addRow("Outer Height", window.outerHeight + " px");

    // Network Info (if supported)
    addCategory("Network");
    const connection = navigator.connection || navigator.webkitConnection || navigator.mozConnection;
    if (connection) {
        addRow("Effective Type", connection.effectiveType);
        addRow("Downlink (Mbps)", connection.downlink);
        addRow("RTT (ms)", connection.rtt);
        addRow("Save Data Mode", connection.saveData ? "Yes" : "No");
    } else {
        addRow("Connection Info", "Not supported");
    }

    // Memory, CPU, and Hardware
    addCategory("Hardware");
    addRow("Device Memory (GB)", navigator.deviceMemory || "N/A");
    addRow("Hardware Concurrency (Threads)", navigator.hardwareConcurrency);
    addRow("Touch Support", 'ontouchstart' in window ? "Yes" : "No");
    addRow("GPU Vendor", getGpuInfo().vendor);
    addRow("GPU Renderer", getGpuInfo().renderer);

    // Location (requires permission)
    addCategory("Geolocation");
    if ("geolocation" in navigator) {
        navigator.geolocation.getCurrentPosition(pos => {
            addRow("Latitude", pos.coords.latitude);
            addRow("Longitude", pos.coords.longitude);
            addRow("Accuracy (m)", pos.coords.accuracy);
        }, err => addRow("Location Access", "Denied or unavailable"));
    } else {
        addRow("Geolocation", "Not supported");
    }

    // IP Info (using public IP API)
    try {
        const ip = await fetch("https://api.ipify.org?format=json").then(r => r.json());
        addRow("Public IP", ip.ip);
    } catch {
        addRow("Public IP", "Unavailable");
    }

    // Time / Locale Info
    addCategory("Time & Locale");
    addRow("Timezone", Intl.DateTimeFormat().resolvedOptions().timeZone);
    addRow("Locale", Intl.DateTimeFormat().resolvedOptions().locale);
    addRow("Current Time", new Date().toString());
}

// Helper to get GPU info
function getGpuInfo() {
    const canvas = document.createElement("canvas");
    const gl = canvas.getContext("webgl") || canvas.getContext("experimental-webgl");
    if (!gl) return {vendor: "Unavailable", renderer: "Unavailable"};
    const debugInfo = gl.getExtension("WEBGL_debug_renderer_info");
    return debugInfo ? {
        vendor: gl.getParameter(debugInfo.UNMASKED_VENDOR_WEBGL),
        renderer: gl.getParameter(debugInfo.UNMASKED_RENDERER_WEBGL)
    } : {vendor: "Unknown", renderer: "Unknown"};
}

document.addEventListener("DOMContentLoaded", () => {
  getInfo(); // everything else stays the same
});

