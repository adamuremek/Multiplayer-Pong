using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Godot;

public class Global : Node
{
	public static Dictionary<int, ServerInfo> gameServers = new Dictionary<int, ServerInfo>();
	public static PlayerState playerState = new PlayerState();
	public static GameState gameState;
	public static HubServerHandle hub = new HubServerHandle();
	public static GameServerHandle gs = new GameServerHandle();
	public static Node serverList;
	public static Node selfRef;

	public static Node2D menuScene;
	public static Node2D menu1;
	public static Node2D menu2;
	public static Node2D gameScene;

	public static void loadMenu1()
	{
		menuScene.Visible = true;
		menu1.Visible = true;
		menu2.Visible = false;
		gameScene.Visible = false;
	}

	public static void loadMenu2()
	{
		menuScene.Visible = true;
		menu1.Visible = false;
		menu2.Visible = true;
		gameScene.Visible = false;
	}

	public static void loadGameScene()
	{
		menuScene.Visible = false;
		menu1.Visible = true;
		menu2.Visible = false;
		gameScene.Visible = true;
	}

	public override void _Ready()
	{
		gameScene = GetTree().Root.GetNode("Main").GetNode("Game") as Node2D;

		selfRef = this;
	}

	public void cleanup()
	{
		if (hub.isActive)
			hub.endHandle();
	}
}



