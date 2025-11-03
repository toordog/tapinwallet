document.addEventListener("DOMContentLoaded", () => {

  async function connect() {
  
    xrpl.connect("https://s.altnet.rippletest.net:51234",function(result) {
       
       document.getElementById("networkStatus").textContent = result
       
       xrpl.createWallet(function(addr) {
            document.getElementById("address").textContent = addr
            
            xrpl.fundTestnet(function(fund) {
            
            xrpl.log('>>>>>>>>>>>>>>>> '+fund);
            
                xrpl.getBalance(function(bal) {
                xrpl.log('>>>>>>>>>>>>>>>> '+bal);
                    document.getElementById("balance").textContent = bal
                }); 
                
            });
            
       });
       
    });
    
//    window.tapin.encrypt("Michael", function (data,i) {
//        window.tapin.log(data);
//    });
  
//    try {
//      const result = await window.tapin.connect("https://s.altnet.rippletest.net:51234")
//      document.getElementById("networkStatus").textContent = result
//
//      const addr = await window.tapin.createWallet()
//      document.getElementById("address").textContent = addr
//      const bal = await window.tapin.getBalance()
//
//      document.getElementById("balance").textContent = bal
//
//    } catch (e) {
//      document.getElementById("networkStatus").textContent = "Error: " + e
//    }
  }

  async function refreshBalance() {
    try {
      const bal = await xrpl.getBalance()
      document.getElementById("balance").textContent = bal
    } catch (e) {
      console.error(e)
    }
  }

  async function send() {
    const to = document.getElementById("toAddress").value.trim()
    const amt = document.getElementById("amount").value.trim()
    const statusEl = document.getElementById("sendStatus")

    if (!to || !amt) {
      statusEl.textContent = "Enter recipient and amount."
      return
    }

    statusEl.textContent = "Sending..."

    try {
      const result = await xrpl.send(to, amt)
      statusEl.textContent = result
      refreshBalance()
    } catch (e) {
      statusEl.textContent = "Error: " + e
    }
  }

  // Wire up buttons
  document.getElementById("connectBtn").addEventListener("click", connect)
  document.getElementById("sendBtn").addEventListener("click", send)
  document.getElementById("refreshBtn").addEventListener("click", refreshBalance)
})

