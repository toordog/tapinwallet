(function(){
  "use strict";

  var LS_KEY = "tp_pm_proofs";
  var state = { proofs: [], filter: "", status: "" };
  var el = function(id){ return document.getElementById(id); };

  function load() {
    try { state.proofs = JSON.parse(localStorage.getItem(LS_KEY) || "[]"); } catch(e){ state.proofs = []; }
    if (state.proofs.length === 0) seed();
  }
  function save(){ localStorage.setItem(LS_KEY, JSON.stringify(state.proofs)); }

  function seed(){
    var now = Date.now();
    state.proofs = [
      mkProof("DID Ownership", 30, "Base DID proof"),
      mkProof("KYC Minimal", 7, "Age > 18 predicate"),
      mkProof("Subscription Status", 1, "ZK membership")
    ];
    // Tweak statuses
    state.proofs[1].status = "revoked";
    state.proofs[2].expiresAt = new Date(now - 24*3600*1000).toISOString(); // expired
    save();
  }

  function mkProof(name, days, notes){
    var now = Date.now();
    return {
      id: "p_" + now + "_" + Math.floor(Math.random()*1e9),
      name: name,
      hash: randHex(64),
      createdAt: new Date(now).toISOString(),
      expiresAt: new Date(now + days*24*3600*1000).toISOString(),
      status: "valid",
      notes: notes || ""
    };
  }

  function randHex(n){
    var s = "", hex = "abcdef0123456789";
    for (var i=0;i<n;i++) s += hex[(Math.random()*hex.length)|0];
    return s;
  }

  function fmtDate(iso){
    try { var d = new Date(iso); return d.toISOString().slice(0,10); } catch(e){ return ""; }
  }

  function visible(proof){
    var t = state.filter.trim().toLowerCase();
    var okT = !t || (proof.name.toLowerCase().indexOf(t) !== -1) || (proof.hash.toLowerCase().indexOf(t) !== -1);
    var okS = !state.status || proof.status === state.status;
    return okT && okS;
  }

  function render(){
    var list = el("list");
    var items = state.proofs.filter(visible);
    el("count").textContent = "(" + items.length + ")";
    if (items.length === 0){
      list.innerHTML = '<li class="item"><div class="icon">∅</div><div class="main"><div class="title2">No proofs</div><div class="sub">Create one with “New Proof”.</div></div><div></div></li>';
      return;
    }
    var html = [];
    for (var i=0;i<items.length;i++){
      var p = items[i];
      html.push(
        '<li class="item '+esc(p.status)+'" data-id="'+esc(p.id)+'">'+
          '<div class="icon">P</div>'+
          '<div class="main">'+
            '<div class="title2">'+esc(p.name)+'</div>'+
            '<div class="sub mono">'+esc(p.hash.slice(0,12))+'… • created '+fmtDate(p.createdAt)+' • exp '+fmtDate(p.expiresAt)+'</div>'+
          '</div>'+
          '<div class="badge '+esc(p.status)+'">'+p.status+'</div>'+
          '<div class="row-actions">'+
            btn("Validate","validate")+
            btn("Revoke","revoke")+
            btn("Details","details")+
          '</div>'+
        '</li>'
      );
    }
    list.innerHTML = html.join("");
    wireRowActions();
  }

  function btn(label,action){
    return '<button class="btn tiny" data-action="'+action+'">'+label+'</button>';
  }

  function wireRowActions(){
    var list = el("list");
    list.onclick = function(ev){
      var target = ev.target || ev.srcElement;
      if (target && target.dataset && target.dataset.action){
        var li = target.closest ? target.closest("li") : findLI(target);
        if (!li) return;
        var id = li.getAttribute("data-id");
        var action = target.dataset.action;
        if (action === "validate") return doValidate(id);
        if (action === "revoke") return doRevoke(id);
        if (action === "details") return openDetails(id);
      }
    };
  }

  function findLI(node){
    while (node && node.tagName !== "LI") node = node.parentNode;
    return node;
  }

  function getById(id){
    for (var i=0;i<state.proofs.length;i++) if (state.proofs[i].id === id) return state.proofs[i];
    return null;
  }

  function doValidate(id){
    var p = getById(id); if (!p) return;
    var now = Date.now();
    var exp = new Date(p.expiresAt).getTime();
    p.status = (now > exp) ? "expired" : "valid";
    save(); render();
  }

  function doRevoke(id){
    var p = getById(id); if (!p) return;
    p.status = "revoked";
    save(); render();
  }

  function openDetails(id){
    var p = getById(id); if (!p) return;
    var body = el("detailBody");
    body.textContent = JSON.stringify(p, null, 2);
    body.setAttribute("data-id", p.id);
    el("detail").setAttribute("aria-hidden","false");
  }

  function closeDetails(){
    el("detail").setAttribute("aria-hidden","true");
  }

  function exportCurrent(){
    var id = el("detailBody").getAttribute("data-id");
    var p = getById(id); if (!p) return;
    var blob = new Blob([JSON.stringify(p,null,2)], {type:"application/json"});
    var a = document.createElement("a");
    a.href = URL.createObjectURL(blob);
    a.download = (p.name || "proof") + ".json";
    document.body.appendChild(a);
    a.click();
    a.parentNode.removeChild(a);
    setTimeout(function(){ URL.revokeObjectURL(a.href); }, 250);
  }

  function copyCurrent(){
    var id = el("detailBody").getAttribute("data-id");
    var p = getById(id); if (!p) return;
    var txt = JSON.stringify(p,null,2);
    if (navigator.clipboard && navigator.clipboard.writeText){
      navigator.clipboard.writeText(txt);
    } else {
      var ta = document.createElement("textarea");
      ta.value = txt; document.body.appendChild(ta); ta.select();
      try { document.execCommand("copy"); } catch(e){}
      document.body.removeChild(ta);
    }
  }

  function showNewForm(show){
    el("newForm").style.display = show ? "block" : "none";
    if (show) el("pName").focus();
  }

  function saveNew(){
    var name = (el("pName").value || "").trim();
    var days = parseInt(el("pDays").value || "30", 10);
    var notes = (el("pNotes").value || "").trim();
    if (!name) return;
    state.proofs.unshift(mkProof(name, Math.max(1,days), notes));
    save(); render(); showNewForm(false);
    el("pName").value = ""; el("pDays").value = "30"; el("pNotes").value = "";
  }

  function doImportFile(){
    el("fileImport").click();
  }
  function handleFile(ev){
    var f = ev.target.files && ev.target.files[0]; if (!f) return;
    var reader = new FileReader();
    reader.onload = function(){
      try{
        var data = JSON.parse(reader.result);
        var arr = Array.isArray(data) ? data : [data];
        for (var i=0;i<arr.length;i++){
          var p = arr[i];
          if (!p.id) p.id = "p_" + Date.now() + "_" + Math.floor(Math.random()*1e9);
          if (!p.status) p.status = "valid";
          state.proofs.unshift(p);
        }
        save(); render();
      }catch(e){}
      ev.target.value = "";
    };
    reader.readAsText(f);
  }

  function esc(s){ return String(s).replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/"/g,"&quot;"); }

  function initUI(){
    el("btnNew").onclick = function(){ showNewForm(true); };
    el("cancelNew").onclick = function(){ showNewForm(false); };
    el("saveProof").onclick = saveNew;

    el("btnImport").onclick = doImportFile;
    el("fileImport").addEventListener("change", handleFile, false);

    el("search").oninput = function(){ state.filter = this.value; render(); };
    el("statusFilter").onchange = function(){ state.status = this.value; render(); };

    el("closeDetail").onclick = closeDetails;
    el("btnExport").onclick = exportCurrent;
    el("btnCopy").onclick = copyCurrent;
  }

  // boot
  load();
  initUI();
  render();
})();

