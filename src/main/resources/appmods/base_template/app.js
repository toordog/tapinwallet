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

    function runDemo() {
        window.tapin.encrypt("Michael", function (data,i) {
            var clicks = readClicks();
            clicks.unshift({at: new Date().toISOString()});
            writeClicks(clicks);

            var out = $("output");
            out.textContent = "Demo ran " + clicks.length + " time(s). Last: " + clicks[0].at + " : " + data + " : "+i;
            out.className = "output pulse";
            setTimeout(function () {
                out.className = "output";
            }, 500);
        });
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

