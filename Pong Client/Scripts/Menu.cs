using Godot;
using System;

public class Menu : Node
{
    // Declare member variables here. Examples:
    // private int a = 2;
    // private string b = "text";
    private Button btn_Connect;
    

    // Called when the node enters the scene tree for the first time.
    public override void _Ready()
    {
        //btn_Connect = GetNode<Button>("Menu/M1/BTN_Connect");
    }

//  // Called every frame. 'delta' is the elapsed time since the previous frame.
//  public override void _Process(float delta)
//  {
//      
//  }

    public void onConnectButtonPress()
    {
        GD.Print("BUTTON PRESSED");
    }
}
