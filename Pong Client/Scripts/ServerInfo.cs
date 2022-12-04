using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Godot;

public class ServerInfo : Node
{
	public int identifier;
	private string serverName;
	private string serverAddr;
	private int serverPort;
	private string player1Name;
	private string player2Name;
	private Color player1Color;
	private Color player2Color;
	private bool isFull;

	private Node serverInfoNode;
	private Label lblServerName;
	private Label lblP1Name;
	private Label lblP2Name;
	private Button btnJoin;


	public ServerInfo(byte[] data)
	{
		//Load the server list module
		serverInfoNode = GD.Load<PackedScene>("res://Scenes/ServerInfo.tscn").Instance();
		

		//Populate references
		lblServerName = serverInfoNode.GetChild<Label>(0);
		lblP1Name = serverInfoNode.GetChild<Label>(2);
		lblP2Name = serverInfoNode.GetChild<Label>(4);
		btnJoin = serverInfoNode.GetChild<Button>(6);
		btnJoin.Connect("button_up", this, "joinBtnDown");

		//Set server info data
		updateData(data);
		Global.serverList.AddChild(serverInfoNode);
	}

	public static int getIdentifier(byte[] data)
	{
		return BitConverter.ToInt32(new byte[] { data[359], data[358], data[357], data[356] }, 0);
	}

	public void updateData(byte[] data)
	{
		deserialize(data);

		lblServerName.Text = serverName;
		lblP1Name.Text = player1Name;
		lblP2Name.Text = player2Name;
		lblP1Name.AddColorOverride("font_color", player1Color);
		lblP2Name.AddColorOverride("font_color", player2Color);
		btnJoin.Disabled = isFull;

		GD.Print(player1Color);
	}

	public void remove()
	{
		serverInfoNode.QueueFree();
	}

	public void deserialize(byte[] data)
	{
		serverName = Encoding.ASCII.GetString(data, 0, 30);
		player1Name = Encoding.ASCII.GetString(data, 30, 30);
		player2Name = Encoding.ASCII.GetString(data, 60, 30);
		player1Color = new Color(data[90] / 255f, data[91] / 255f, data[92] / 255f);
		player2Color = new Color(data[93] / 255f, data[94] / 255f, data[95] / 255f);
		serverAddr = Encoding.ASCII.GetString(data, 96, 256);
		serverPort = BitConverter.ToInt32(new byte[] { data[355], data[354], data[353], data[352] }, 0);
		identifier = BitConverter.ToInt32(new byte[] { data[359], data[358], data[357], data[356] }, 0);
		isFull = BitConverter.ToBoolean(data, 360);
	}

	public void joinBtnDown()
	{
		Global.gs.connect(serverAddr, serverPort);
	}
}
