(function () {
  "use strict";

  function fmtXrp(n) {
    return (Math.round(n * 1e6) / 1e6).toFixed(6) + " XRP";
  }

  function fmtDate(ts) {
    var d = new Date(ts);
    var y = d.getFullYear();
    var m = ("0" + (d.getMonth() + 1)).slice(-2);
    var dd = ("0" + d.getDate()).slice(-2);
    var hh = ("0" + d.getHours()).slice(-2);
    var mm = ("0" + d.getMinutes()).slice(-2);
    return y + "-" + m + "-" + dd + " " + hh + ":" + mm;
  }

  var state = {
    accountName: "Main Account",
    address: "rEXAMPLEXXXXXXXXXXXXXXXXXXXXXXX",
    balance: 1253.432178,
    txs: [
      { id: "A1", type: "recv", counterparty: "rAlice...", memo: "Payment", amount: 25.5, time: Date.now() - 3600 * 1000 },
      { id: "B2", type: "sent", counterparty: "rBob...", memo: "Invoice #42", amount: -5.75, time: Date.now() - 7200 * 1000 },
      { id: "C3", type: "recv", counterparty: "rCarol...", memo: "Refund", amount: 2.1, time: Date.now() - 86400 * 1000 }
    ]
  };

  function byId(id) { return document.getElementById(id); }

  function render() {
    byId("accountName").textContent = state.accountName;
    byId("balance").textContent = fmtXrp(state.balance);
    byId("address").textContent = state.address;

    var ul = byId("txList");
    while (ul.firstChild) ul.removeChild(ul.firstChild);

    for (var i = 0; i < state.txs.length; i++) {
      var tx = state.txs[i];

      var li = document.createElement("li");
      li.className = "tx-item " + (tx.amount < 0 ? "sent" : "recv");
      li.setAttribute("data-id", tx.id);

      var icon = document.createElement("div");
      icon.className = "tx-icon";
      icon.textContent = tx.amount < 0 ? "↑" : "↓";

      var main = document.createElement("div");
      main.className = "tx-main";

      var title = document.createElement("div");
      title.className = "tx-title";
      title.textContent = tx.counterparty;

      var sub = document.createElement("div");
      sub.className = "tx-sub";
      sub.textContent = (tx.memo || "—") + " • " + fmtDate(tx.time);

      main.appendChild(title);
      main.appendChild(sub);

      var amt = document.createElement("div");
      amt.className = "tx-amt " + (tx.amount < 0 ? "negative" : "positive");
      amt.textContent = (tx.amount < 0 ? "" : "+") + fmtXrp(tx.amount);

      li.appendChild(icon);
      li.appendChild(main);
      li.appendChild(amt);

      li.addEventListener("click", (function (id) {
        return function () {
          // placeholder: open tx details
          console.log("Open TX:", id);
        };
      })(tx.id));

      ul.appendChild(li);
    }
  }

  function showReceive(show) {
    byId("receiveCard").hidden = show ? null : "hidden";
    byId("sendCard").hidden = "hidden";
  }

  function showSend(show) {
    byId("sendCard").hidden = show ? null : "hidden";
    byId("receiveCard").hidden = "hidden";
  }

  function copyText(text) {
    try {
      navigator.clipboard.writeText(text);
    } catch (e) {
      var ta = document.createElement("textarea");
      ta.value = text;
      document.body.appendChild(ta);
      ta.select();
      try { document.execCommand("copy"); } catch (e2) {}
      document.body.removeChild(ta);
    }
  }

  document.addEventListener("DOMContentLoaded", function () {
    render();

    byId("btnReceive").addEventListener("click", function () { showReceive(true); });
    byId("btnHideReceive").addEventListener("click", function () { showReceive(false); });
    byId("btnCopy").addEventListener("click", function () { copyText(state.address); });

    byId("btnSend").addEventListener("click", function () { showSend(true); });
    byId("btnCancelSend").addEventListener("click", function () { showSend(false); });

    byId("btnConfirmSend").addEventListener("click", function () {
      var to = byId("sendTo").value;
      var amt = parseFloat(byId("sendAmount").value || "0");
      var memo = byId("sendMemo").value;

      if (!to || !(amt > 0)) { console.log("Invalid send"); return; }

      state.balance -= amt;
      state.txs.unshift({
        id: "T" + (Date.now()),
        type: "sent",
        counterparty: to,
        memo: memo,
        amount: -amt,
        time: Date.now()
      });

      showSend(false);
      byId("sendTo").value = "";
      byId("sendAmount").value = "";
      byId("sendMemo").value = "";
      render();
    });

    byId("btnRefresh").addEventListener("click", function () {
      // placeholder: refresh from network
      render();
    });
  });
})();
