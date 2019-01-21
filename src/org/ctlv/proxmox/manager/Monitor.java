package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Monitor implements Runnable {

	Analyzer analyzer;
	ProxmoxAPI api;
	
	public Monitor(ProxmoxAPI api, Analyzer analyzer) {
		this.api = api;
		this.analyzer = analyzer;
	}
	

	@Override
	public void run() {
		
		while(true) {
			
			Map<String, List<LXC>> myCTsPerServer = new HashMap<>();
			// Récupérer les données sur les serveurs
			//Map<String, List<LXC>> myCTsPerServer;
			//CT du serveur 1
			String srv1 = Constants.SERVER1;
			List<LXC> cts1 = new ArrayList<LXC>();
			try {
				cts1 = api.getCTs(srv1);
			} catch (LoginException | JSONException | IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			//CT du serveur 2
			String srv2 = Constants.SERVER2;
			List<LXC> cts2 = new ArrayList<LXC>();
			try {
				cts2 = api.getCTs(srv1);
			} catch (LoginException | JSONException | IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			myCTsPerServer.put(srv1,cts1);
			myCTsPerServer.put(srv2,cts2);
			
			// Lancer l'analyse
			try {
				analyzer.analyze(myCTsPerServer);
			} catch (LoginException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
			// attendre une certaine période
			try {
				Thread.sleep(Constants.MONITOR_PERIOD * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
