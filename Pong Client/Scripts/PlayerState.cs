using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

public class PlayerState
{
    private const short PLAYER_STATE_BUF_SIZE = 42;
    private const short MAX_STRING_SIZE = 30;
    public byte playerIdentifier { get; set; } = 0;
    public string playerName { get; set; } = "";
    public byte[] playerColor { get; set; } = { 0, 0, 0 };
    public float paddlePosX { get; set; } = 0f;
    public float paddlePosY { get; set; } = 240f;

    public byte[] serialize()
    {
        byte[] data = new byte[PLAYER_STATE_BUF_SIZE];
        short counter = 0;

        //Serialize identifier
        data[0] = playerIdentifier;
        counter += 1;

        //Serialize player name;
        byte[] bytes = Encoding.ASCII.GetBytes(playerName);
        for(int i = 0; i < MAX_STRING_SIZE; i++)
        {
            if(i < bytes.Length)
                data[counter + i] = bytes[i];
            else
                data[counter + i] = 0;
        }
        counter += MAX_STRING_SIZE;

        //SerializeColor
        for(int i = 0; i < playerColor.Length; i++)
        {
            data[counter + i] = playerColor[i];
        }
        counter += 3;

        //Serialize paddle x position
        bytes = BitConverter.GetBytes(paddlePosX);
        Array.Reverse(bytes);
        for (int i = 0; i < bytes.Length; i++)
        {
            data[counter + i] = bytes[i];
        }
        counter += 4;

        //Serialize paddle y position
        bytes = BitConverter.GetBytes(paddlePosY);
        Array.Reverse(bytes);
        for (int i = 0; i < bytes.Length; i++)
        {
            data[counter + i] = bytes[i];
        }
        counter += 4;

        return data;
    }
}
