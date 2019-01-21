package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Analyzer {
	ProxmoxAPI api;
	Controller controller;
	
	public Analyzer(ProxmoxAPI api, Controller controller) {
		this.api = api;
		this.controller = controller;
	}
	
	public void analyze(Map<String, List<LXC>> myCTsPerServer) throws LoginException, JSONException, IOException  {
	//public void analyze() throws LoginException, JSONException, IOException  {

		// Calculer la quantité de RAM utilisée par mes CTs sur chaque serveur
		long memOnServer1 = 0;
		String srv1 = Constants.SERVER1;
		List<LXC> cts = api.getCTs(srv1);
		for (LXC lxc : cts) {
			memOnServer1+=lxc.getMem();
		}
		
		long memOnServer2 = 0;
		String srv2 = Constants.SERVER2;
		cts = api.getCTs(srv2);
		for (LXC lxc : cts) {
			memOnServer2+=lxc.getMem();
		}

		
		// Mémoire autorisée sur chaque serveur
		long memAllowedOnServer1 = (long) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.MAX_THRESHOLD);
		long memAllowedOnServer2 = (long) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.MAX_THRESHOLD);

		
		// Analyse et Actions
		if(memOnServer1>=memAllowedOnServer1*0.5){
			controller.migrateFromTo(srv1,srv2);
		}
		
		if(memOnServer2>=memAllowedOnServer2*0.5){
			controller.migrateFromTo(srv2,srv1);
		}
		
		if(memOnServer1>=memAllowedOnServer1*0.75){
			controller.offLoad(srv1);
		}
		
		if(memOnServer2>=memAllowedOnServer2*0.75){
			controller.offLoad(srv2);
		}
		
	}

}
