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
    Color p1Color;
    Color p2Color;
    Node2D p1Paddle;
    Node2D p2Paddle;
    Node2D ball;


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
            GD.Print(Global.playerState.paddlePosY);
        }
    }

    public void updateData(byte[] data)
    {
        txt_p1Name.Text = Encoding.ASCII.GetString(data, 1, 30);
        txt_p2Name.Text = Encoding.ASCII.GetString(data, 31, 30);
        txt_p1Score.Text = BitConverter.ToInt32(data, 61).ToString();
        txt_p1Score.Text = BitConverter.ToInt32(data, 65).ToString();
        p1Color = p1Color == null ? new Color(data[69] / 255f, data[70] / 255f, data[71] / 255f) : p1Color;
        p2Color = p2Color == null ? new Color(data[72] / 255f, data[73] / 255f, data[74] / 255f) : p2Color;

        if(p1Color != null)
            txt_p1Score.AddColorOverride("font_color", p1Color);
        if(p2Color != null)
            txt_p2Score.AddColorOverride("font_color", p2Color);

        p1Paddle.Position = new Vector2(p1Paddle.Position.x, BitConverter.ToSingle(data, 75));
        p2Paddle.Position = new Vector2(p2Paddle.Position.x, BitConverter.ToSingle(data, 79));
        ball.Position = new Vector2(BitConverter.ToSingle(data, 83), BitConverter.ToSingle(data, 87));
        txt_serverMssg.Text = Encoding.ASCII.GetString(data, 91, 60);
    }
}
