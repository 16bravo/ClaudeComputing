package org.ctlv.proxmox.tester;

import java.io.IOException;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;


public class Main {

	public static void main(String[] args) throws LoginException, JSONException, IOException {

		ProxmoxAPI api = new ProxmoxAPI();		
		
		// Listes les CTs par serveur
		/*for (int i=1; i<=10; i++) {
			String srv ="srv-px"+i;
			System.out.println("CTs sous "+srv);
			List<LXC> cts = api.getCTs(srv);
			
			for (LXC lxc : cts) {
				System.out.println("\t" + lxc.getName());
			}
		}*/
		
		//CPU Usage
		String srv = "srv-px7";
		int max = 0;
		List<LXC> cts = api.getCTs(srv);
		for (LXC lxc : cts) {
			/*System.out.println("\n" +"\t" +lxc.getName() +", utilisation CPU : "+ lxc.getCpu() + ", nb : "+lxc.getCpus());
			System.out.println("\t" +lxc.getName() +", utilisation disque je crois : "+ ((lxc.getDiskwrite()+lxc.getDiskread())*10000/lxc.getMaxdisk()));
			System.out.println("\t" +lxc.getName() +", utilisation mem : "+ ((lxc.getMem()*10000)/lxc.getMaxmem()));*/
			if(Integer.parseInt(lxc.getVmid())>=2201 && Integer.parseInt(lxc.getVmid())<2300){
			//System.out.println("\t" +lxc.getName() +", id : "+ lxc.getVmid());
			//int val = Integer.parseInt(lxc.getVmid())-2200;
			//if(val>max){max = val ;}
				api.startCT("srv-px7", lxc.getVmid());
			}
		}
		
		//System.out.println(max);
		
		//List des CTS et de leurs identifiants
		/*List<LXC> cts = api.getCTs("srv-px7");
		for (LXC lxc : cts) {
			System.out.println(lxc);
		}*/
		
		
		// Créer un CT
		//api.createCT("srv-px7", "2200", "ct-tpiss-virt-B2-ct2", 512);
		//Start CT
		/*api.startCT("srv-px7", "2200");
		//Récupérer des informations
		System.out.println("\n Status : "+ api.getCT("srv-px7", "2200").getStatus());
		System.out.println("\n CPU : "+ api.getCT("srv-px7", "2200").getCpu());
		System.out.println("\n Disk : "+ api.getCT("srv-px7", "2200").getDiskwrite());
		System.out.println("\n Memory : "+ ((api.getCT("srv-px7", "2200").getMem()*10000)/api.getCT("srv-px7", "2200").getMaxmem()));
		//Stop CT
		api.stopCT("srv-px7", "2200");*/
		
		
		// Supprimer un CT
		//api.deleteCT("srv-px8", "2203");
		
	}

}
