package com.icey.apps.utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.IntMap;

/** A custom number keyboard that implements InputProcessor
 *
 * Created by Allen on 3/2/15.
 */
public class NumberKeyboard extends Window implements InputProcessor{

    Skin skin;

    Table table; //main button table
    IntMap<TextButton> numButtons = new IntMap<TextButton>(); //array storing number buttons\

    TextField textField; //textField doing the listening

    String[][] digits = {{"1", "2", "3"}, {"4", "5", "6"}, {"7", "8", "9"}, {"0", ".", ""}};

    public NumberKeyboard(Skin skin, TextField textField){
        super("", skin);
        this.skin = skin;

        setBounds(0, 0, Constants.SCREEN_WIDTH, 300);
    }

    protected void setTable(){

        table = getButtonTable();
        table.setSize(getWidth(), getHeight());

        int num = 0;
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                TextButton numButton = new TextButton(digits[i][j], skin, "small");
                numButtons.put(num, numButton);

                num++;

                table.add(numButton).height(20).width(20).pad(2);
            }
            table.row();
        }

    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {


        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
