package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Controller {

	ProxmoxAPI api;
	public Controller(ProxmoxAPI api){
		this.api = api;
	}
	
	// migrer un conteneur du serveur "srcServer" vers le serveur "dstServer"
	public void migrateFromTo(String srcServer, String dstServer) throws LoginException, JSONException, IOException  {
		System.out.println(Integer.toString(2200+minCT(srcServer)));
		System.out.println(api.getCT(srcServer, Integer.toString(2200+minCT(srcServer))).getStatus());
		api.stopCT(srcServer, Integer.toString(2200+minCT(srcServer)));
		api.migrateCT(srcServer, Integer.toString(2200+minCT(srcServer)), dstServer);
		System.out.println(api.getCT(srcServer, Integer.toString(2200+minCT(srcServer))).getStatus());
	}

	// arrêter le plus vieux conteneur sur le serveur "server"
	public void offLoad(String server) throws LoginException, JSONException, IOException {
		api.stopCT(server, Integer.toString(2200+minCT(server)));
	}
	
	//recuperer le nombre le plus petit généré pour nos CTs actives
	public int minCT(String srv) throws LoginException, JSONException, IOException{
				int min = 99;
				List<LXC> cts = api.getCTs(srv);
				for (LXC lxc : cts) {
					if(Integer.parseInt(lxc.getVmid())>=2200 && Integer.parseInt(lxc.getVmid())<2300 && lxc.getStatus().equals("running")){
					System.out.println("\t" +lxc.getName() +", id : "+ lxc.getVmid()+", status : "+ lxc.getStatus());
					int val = Integer.parseInt(lxc.getVmid())-2200;
					if(val<min){min = val ;}
					}
				}
				return min;
	}

}
