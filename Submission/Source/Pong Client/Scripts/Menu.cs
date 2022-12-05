using Godot;
using System;
using System.Collections.Generic;

public class Menu : Node2D
{

	private LineEdit txt_playerName;
	private LineEdit txt_hubAddr;
	private LineEdit txt_hubPort;

	private byte r = 0, g = 0, b = 0;
	
	public override void _Ready()
	{
		txt_playerName = GetTree().Root.GetNode("Main").GetNode<LineEdit>("MenuUI/M1/Fields/TXT_Player_Name");
		txt_hubAddr = GetTree().Root.GetNode("Main").GetNode<LineEdit>("MenuUI/M1/Fields/TXT_Addr");
		txt_hubPort = GetTree().Root.GetNode("Main").GetNode<LineEdit>("MenuUI/M1/Fields/TXT_Port");

		Global.menuScene = this;
		Global.menu1 = GetTree().Root.GetNode("Main").GetNode("MenuUI/M1") as Node2D;
		Global.menu2 = GetTree().Root.GetNode("Main").GetNode("MenuUI/M2") as Node2D;
		Global.serverList = GetTree().Root.GetNode("Main").GetNode("MenuUI/M2/ServerList/VBoxContainer") as VBoxContainer;
	}

	private void redChanged(float value)
	{
		r = (byte)value;
		updatePlayerNameColor();
	}

	private void greenChanged(float value)
	{
		g = (byte)value;
		updatePlayerNameColor();
	}


	private void blueChanged(float value)
	{
		b = (byte)value;
		updatePlayerNameColor();
	}


	private void updatePlayerNameColor()
	{
		txt_playerName.AddColorOverride("font_color", new Color(((float)r/255f), ((float)g / 255f), ((float)b / 255f)));
	}

	public void onConnectButtonPress()
	{
		Global.playerState.playerName = txt_playerName.Text;
		Global.playerState.playerColor = new byte[] {r,g,b};

		
		if (!txt_hubAddr.Text.Equals("") && !txt_hubPort.Text.Equals("") && Global.hub.connect(txt_hubAddr.Text, Int32.Parse(txt_hubPort.Text)))
		{
			Global.loadMenu2();
		}
	}

	private void goBack()
	{
		Global.hub.endHandle();
		foreach (KeyValuePair<int, ServerInfo> entry in Global.gameServers)
		{
			entry.Value.remove();
		}
		Global.gameServers.Clear();

		Global.loadMenu1();
	}

	private void refreshServerList()
	{
		if(Global.hub.isActive)
		{
			foreach(KeyValuePair<int, ServerInfo> entry in Global.gameServers)
			{
				entry.Value.remove();
			}

			Global.gameServers.Clear();

			Global.hub.requestServerList();
		}
	}
}






