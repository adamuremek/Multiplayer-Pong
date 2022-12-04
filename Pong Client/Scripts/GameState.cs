using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Godot;


public class GameState : Node2D
{
	public const int GAME_STATE_SIZE = 151;
	Label txt_p1Name;
	Label txt_p2Name;
	Label txt_p1Score;
	Label txt_p2Score;
	Label txt_serverMssg;
	Node2D p1Paddle;
	Node2D p2Paddle;
	Node2D ball;

	AudioStreamPlayer2D paddle1Hit;
	AudioStreamPlayer2D paddle2Hit;
	AudioStreamPlayer2D topHit;
	AudioStreamPlayer2D bottomHit;
	AudioStreamPlayer2D score;

	private int p1Score = 0;
	private int p2Score = 0;
	private byte[] p1Color = { 0, 0, 0 };
	private byte[] p2Color = { 0, 0, 0 };
	private string p1Name = "";
	private string p2Name = "";

	//player identifier - 0
	//p1 name - 1-30
	//p2 name - 31-60
	//p1 score - 61-64
	//p1 score - 65-68
	//p1 color - 69-71
	//p2 color - 72-74
	//p1 paddle height - 75-78
	//p2 paddle height - 79-82
	//ball x pos - 83-86
	//ball y pos - 87-90
	//Server message - 91-150

	public override void _Ready()
	{
		txt_p1Name = GetTree().Root.GetNode("Main").GetNode<Label>("Game/UI/LBL_p1Name");
		txt_p2Name = GetTree().Root.GetNode("Main").GetNode<Label>("Game/UI/LBL_p2Name");
		txt_p1Score = GetTree().Root.GetNode("Main").GetNode<Label>("Game/UI/LBL_p1Score");
		txt_p2Score = GetTree().Root.GetNode("Main").GetNode<Label>("Game/UI/LBL_p2Score");
		txt_serverMssg = GetTree().Root.GetNode("Main").GetNode<Label>("Game/UI/LBL_serverMssg");

		p1Paddle = GetTree().Root.GetNode("Main").GetNode("Game/Player1Paddle") as Node2D;
		p2Paddle = GetTree().Root.GetNode("Main").GetNode("Game/Player2Paddle") as Node2D;
		ball = GetTree().Root.GetNode("Main").GetNode("Game/Ball") as Node2D;

		paddle1Hit = GetNode("Player1Paddle/P1PHit") as AudioStreamPlayer2D;
		paddle2Hit = GetNode("Player2Paddle/P2PHit") as AudioStreamPlayer2D;
		topHit = GetNode("Boundary/TopHit") as AudioStreamPlayer2D;
		bottomHit = GetNode("Boundary/BottomHit") as AudioStreamPlayer2D;
		score = GetNode("Boundary/Score") as AudioStreamPlayer2D;


		Global.gameState = this;
		
	}

	public override void _Process(float delta)
	{
		if (this.Visible && Global.gs.isActive)
			Global.gs.updateServer();
	}

	public override void _Input(InputEvent @event)
	{
		if (!this.Visible)
			return;

		if (@event.IsActionPressed("disconnect"))
		{
			Global.gs.endHandle();
			Global.loadMenu2();
		}

		if(@event is InputEventMouseMotion mouse)
		{
			Global.playerState.paddlePosY = Mathf.Clamp(Global.playerState.paddlePosY + mouse.Relative.y, 35f, 445f);
		}
	}

	private void leftPaddleHit(object area)
	{
		Area2D a = area as Area2D;
		if(a.Name.Equals("Ball"))
			paddle1Hit.Play();
	}

	private void rightPaddleHit(object area)
	{
		Area2D a = area as Area2D;
		if (a.Name.Equals("Ball"))
			paddle2Hit.Play();
	}

	private void topBoundHit(object area)
	{
		topHit.Play();
	}

	private void bottomBoundHit(object area)
	{
		bottomHit.Play();
	}

	private void scoreHit(object area)
	{
		score.Play();
	}

	public void updateData(byte[] data)
	{
		//Set names
		string newP1Name = Encoding.ASCII.GetString(data, 1, 30);
		string newP2Name = Encoding.ASCII.GetString(data, 31, 30);

        if (!p1Name.Equals(newP1Name))
        {
			p1Name = newP1Name;
			txt_p1Name.Text = p1Name;
        }

		if (!p2Name.Equals(newP2Name))
		{
			p2Name = newP2Name;
			txt_p2Name.Text = p2Name;
		}


		//Set Scores
		int incomingP1Score = BitConverter.ToInt32(new byte[] { data[64], data[63], data[62], data[61] }, 0);
		int incomingP2Score = BitConverter.ToInt32(new byte[] { data[68], data[67], data[66], data[65] }, 0);

		if(incomingP1Score != p1Score)
		{
			p1Score = incomingP1Score;
			txt_p1Score.Text = p1Score.ToString();
		}

		if(incomingP2Score != p2Score)
		{
			p2Score = incomingP2Score;
			txt_p2Score.Text = p2Score.ToString();
		}


		//Set colors
		byte[] newP1Color = new byte[] { data[69], data[70], data[71] };
		byte[] newP2Color = new byte[] { data[72], data[73], data[74] };
		if (!(p1Color[0] == newP1Color[0] && p1Color[1] == newP1Color[1] && p1Color[2] == newP1Color[2]))
		{
			p1Color = newP1Color;
			Color c = new Color(p1Color[0] / 255f, p1Color[1] / 255f, p1Color[2] / 255f);
			txt_p1Score.AddColorOverride("font_color", c);
			txt_p1Name.AddColorOverride("font_color", c);
			Polygon2D paddle = p1Paddle.GetChild(0) as Polygon2D;
			paddle.Color = c;
		}
  
		
		if (!(p2Color[0] == newP2Color[0] && p2Color[1] == newP2Color[1] && p2Color[2] == newP2Color[2]))
		{
			p2Color = newP2Color;
			Color c = new Color(p2Color[0] / 255f, p2Color[1] / 255f, p2Color[2] / 255f);
			txt_p2Score.AddColorOverride("font_color", c);
			txt_p2Name.AddColorOverride("font_color", c);
			Polygon2D paddle = p2Paddle.GetChild(0) as Polygon2D;
			paddle.Color = c;
		}

		p1Paddle.Position = new Vector2(p1Paddle.Position.x, BitConverter.ToSingle(new byte[] { data[78], data[77], data[76], data[75] }, 0));
		p2Paddle.Position = new Vector2(p2Paddle.Position.x, BitConverter.ToSingle(new byte[] { data[82], data[81], data[80], data[79] }, 0));
		ball.Position = new Vector2(BitConverter.ToSingle(new byte[] { data[86], data[85], data[84], data[83] }, 0), BitConverter.ToSingle(new byte[] { data[90], data[89], data[88], data[87] }, 0));
		
		txt_serverMssg.Text = Encoding.ASCII.GetString(data, 91, 60);



	}
}



