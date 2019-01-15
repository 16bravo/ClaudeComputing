package org.ctlv.proxmox.generator;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class GeneratorMain {
	
	static Random rndTime = new Random(new Date().getTime());
	public static int getNextEventPeriodic(int period) {
		return period;
	}
	public static int getNextEventUniform(int max) {
		return rndTime.nextInt(max);
	}
	public static int getNextEventExponential(int inv_lambda) {
		float next = (float) (- Math.log(rndTime.nextFloat()) * inv_lambda);
		return (int)next;
	}
	
	public static void main(String[] args) throws InterruptedException, LoginException, JSONException, IOException {
		
	
		long baseID = Constants.CT_BASE_ID;
		int lambda = 30;
		
		
		Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();

		ProxmoxAPI api = new ProxmoxAPI();
		Random rndServer = new Random(new Date().getTime());
		Random rndRAM = new Random(new Date().getTime()); 
		
		//recuperer le nombre le plus grand généré pour nos CTs
		int max = 0;
		String srv = Constants.SERVER1;
		List<LXC> cts = api.getCTs(srv);
		for (LXC lxc : cts) {
			if(Integer.parseInt(lxc.getVmid())>=2200 && Integer.parseInt(lxc.getVmid())<2300){
			System.out.println("\t" +lxc.getName() +", id : "+ lxc.getVmid());
			int val = Integer.parseInt(lxc.getVmid())-2200;
			if(val>max){max = val ;}
			}
		}
		srv = Constants.SERVER2;
		cts = api.getCTs(srv);
		for (LXC lxc : cts) {
			if(Integer.parseInt(lxc.getVmid())>=2200 && Integer.parseInt(lxc.getVmid())<2300){
			System.out.println("\t" +lxc.getName() +", id : "+ lxc.getVmid());
			int val = Integer.parseInt(lxc.getVmid())-2200;
			if(val>max){max = val ;}
			}
		}
		
		long memAllowedOnServer1 = (long) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.MAX_THRESHOLD);
		long memAllowedOnServer2 = (long) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.MAX_THRESHOLD);
		
		while (true) {
			
			// 1. Calculer la quantité de RAM utilisée par mes CTs sur chaque serveur
			long memOnServer1 = 0;
			srv = Constants.SERVER1;
			cts = api.getCTs(srv);
			for (LXC lxc : cts) {
				memOnServer1+=lxc.getMem();
			}
			
			long memOnServer2 = 0;
			srv = Constants.SERVER2;
			cts = api.getCTs(srv);
			for (LXC lxc : cts) {
				memOnServer2+=lxc.getMem();
			}
			
			// Mémoire autorisée sur chaque serveur
			float memRatioOnServer1 = memOnServer1/memAllowedOnServer1;
			float memRatioOnServer2 = memOnServer2/memAllowedOnServer2;
			
			System.out.println("memory on serv 1 : "+memOnServer1+"/"+memAllowedOnServer1+"("+memRatioOnServer1+")");
			System.out.println("memory on serv 2 : "+memOnServer2+"/"+memAllowedOnServer2+"("+memRatioOnServer2+")");
			
			
			if (memOnServer1 < memAllowedOnServer1 && memOnServer2 < memAllowedOnServer2) {  // Exemple de condition de l'arrêt de la génération de CTs
				
				// choisir un serveur aléatoirement avec les ratios spécifiés 66% vs 33%
				String serverName;
				if (rndServer.nextFloat() < Constants.CT_CREATION_RATIO_ON_SERVER1)
					serverName = Constants.SERVER1;
				else
					serverName = Constants.SERVER2;
				
				// créer un contenaire sur ce serveur
				max++;
				api.createCT(serverName, Long.toString(baseID+max), "ct-tpiss-virt-B2-ct"+max, 512);
												
				// planifier la prochaine création
				int timeToWait = getNextEventExponential(lambda); // par exemple une loi expo d'une moyenne de 30sec
				
				// attendre jusqu'au prochain évènement
				Thread.sleep(1000 * timeToWait);
			}
			else {
				System.out.println("Servers are loaded, waiting ...");
				Thread.sleep(Constants.GENERATION_WAIT_TIME* 1000);
			}
		}
		
	}

}
