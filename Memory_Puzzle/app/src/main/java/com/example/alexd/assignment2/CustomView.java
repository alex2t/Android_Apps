package com.example.alexd.assignment2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class CustomView extends View {

    // Create a rectangle object to drawn...
    Rect card;
    // int color array that contains the 8 different colors to display on click...
    int[] colors = new int[]{Color.CYAN, Color.DKGRAY, Color.GRAY, Color.RED, Color.MAGENTA, Color.LTGRAY, Color.BLUE, Color.YELLOW};
    // Randomly select indices from the colors array and place in this array. every index repeats twice...
    List<Integer> randomIndices;
    // When a pair is matched its indices are saved in this array. So that they won't appear on the board...
    List<Integer> matched = new ArrayList<>();
    // These to stores the scores of PlayerOne and PlayerTwo...
    int playerOneScore = 0;
    int playerTwoScore = 0;
    // These two takes the record of both cards if they have been flipped or not...
    boolean cardOneTurned = false;
    boolean cardTwoTurned = false;
    // The following both contains the indices of those two cards which are flipped...
    int firstTurnedCardIndex = -1;
    int secondTurnedCardIndex = -1;
    // This stores the turn of current player...
    int turn = 1;
    int padding;
    // This stores the height and width of the rectangle to be drawn...
    int rectangleWidth;
    int rectangleHeight;

    /**
     * This just a default constructor...
     * @param context
     */
    public CustomView(Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * @param context
     * @param attrs
     */
    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }


    /**
     * This method is called in every constructor to initialize random indices and
     * background color of the board...
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {
        randomIndices = getrandomIndices(colors);
        setBackgroundColor(Color.WHITE);
    }


    /**
     * This method is called when the view is created. Drawing of rectangles is controlled by this...
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // This is just to give padding on to the rectangle...
        padding = 5;
        // Get the width of the custom view. as we have 4 rectangle in a row so divide that by 4. Also minus the padding...
        rectangleWidth = (getMeasuredWidth() - padding * 2) / 4;
        // The same as above. Get total height of the view divide by 4 as we have 4 rows...
        rectangleHeight = (getMeasuredHeight() - padding * 2) / 4;

        // This loop runs for 4x4 times. as we have 4 rows and 4 cols. on each a iteration a rectangle is drawn...
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                // Firstly create a pain object to paint the rectangle...
                Paint paint = new Paint();

                // The left, top, bottom and right points of that rectangle.
                //  multiply them with i and j to get the value of the next place to  draw the rectangle

                int left = padding + (rectangleWidth * j) + 5;
                int top = (rectangleHeight * i) + 10;
                int bottom = rectangleHeight * (i + 1);
                int right = padding + rectangleWidth * (j + 1);

                // Create a Rect object to draw...
                card = new Rect(left, top, right, bottom);

                // Now the real logic comes here. we have to decide which card will be faced down, which will be face up
                // and which need to be hidden. First we create the current index using values of i and j...
                int currentIndex = i * 4 + j;

                // First check if the currentIndex is equal to any of firstTurnedCardIndex or secondTurnedCardIndex. If yes then get the value
                // from randomIndices of that index and base upon that value get the color of that card from the colors array.
                // Then draw a rectangle of that color on the board...
                if(firstTurnedCardIndex == currentIndex || secondTurnedCardIndex == currentIndex)
                {
                    paint.setColor(colors[randomIndices.get(currentIndex)]);
                    canvas.drawRect(card, paint);
                }
                // Else we will check if the currectIndex is present in the matched array. Which include the indices of pairs
                // which are already matched. If it is present in the matched array draw a rectangle with white color which
                // means that this card is hidden...
                else if (matched.contains(currentIndex))
                {
                    paint.setColor(Color.WHITE);
                    canvas.drawRect(card, paint);
                }
                // esle only draw a face down card as all cards are drawn in the beginning of the game...
                else
                {
                    paint.setColor(Color.parseColor("#FFFFBB33"));
                    canvas.drawRect(card, paint);
                }

            }
        }
    }

    /**
     * This method generates the random indices based on the indices of the colors array. Every index repeated twice
     * as we have two cards of each color...
     * @param colors takes colors array as input...
     * @returns the randomly generated indices array...
     */
    public List<Integer> getrandomIndices(int[] colors)
    {
        List<Integer> indices = new ArrayList<>();
        Random rand = new Random();
        // As we have cards double than the colors length so repeat a loop two times the length of colors array...
        for(int i =0 ; i< colors.length*2; i++)
        {
            // Create a random number from 0 to the length of the colors array...
            int index = rand.nextInt(colors.length);

            // if the randomly generated index is not in the list of indices then add it to the list...
            if(!indices.contains(index)) //
                indices.add(index);
                // If it is already present then we need to check its count. if count is 1 add it otherwise create a next random index...
            else
            {
                // This frequency function gives the count of the index from the indices array...
                int count = Collections.frequency(indices, index);
                // If count is one of that index add it to the list
                if (count == 1)
                    indices.add(index);
                    // else as we have not added this index into the our list so subtract i by 1...
                else
                    i = i - 1;
            }

        }

        // In the end return the randonmly generated array...
        return indices;
    }


    /**
     * This method is called when the screen orientation or resolution is changed. This makes the random view square irrespective
     * of the screen size and orientation of the device...
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // This stores the size of one side of the square...
        int size = 0;
        // Get screen height and width...
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        // check what is less width or height. make the size equal to which is less...
        if (width < height) {
            size = width;
        } else {
            size = height;
        }

        // Then set dimension of the custom view to size*size, which will make it a square in shape...
        setMeasuredDimension(size, size);
    }

    /**
     * This method is called when ever touch event happens...
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_UP:
                if(!cardOneTurned || !cardTwoTurned) {
                    // Get the x and y coordinates of the event...
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    // Now we will again make 4x4 loop, to check that in which rectangle the x, y coordinate falls...
                    for (int i = 0; i < 4; i++) {
                        for (int j = 0; j < 4; j++) {

                            // Create left, right, top and bottom points of each rectangle one by one...
                            int left = padding + (rectangleWidth * j) + 5;
                            int top = (rectangleHeight * i) + 10;
                            int bottom = rectangleHeight * (i + 1);
                            int right = padding + rectangleWidth * (j + 1);

                            // Check if the x, y coordinated falls in the range of above created rectangle...
                            if (x >= left && x <= right && y >= top && y <= bottom) {
                                int currentIndex = i*4+j;

                                // then check if the index is already present in the index array. if yes do nothing.
                                // if yes then the code inside the if will work...
                                if(!matched.contains(currentIndex)) {
                                    // If first card is not turned then make firstTurnedCardIndex equal to the currentIndex and make
                                    // cardIneTurned true and break out of the loop...
                                    if (!cardOneTurned) {
                                        firstTurnedCardIndex = currentIndex;
                                        cardOneTurned = true;
                                        // This will call the onDraw function to recreate the board based on new conditions...
                                        invalidate();
                                        break;
                                    }
                                    // If first card is turned and this currentIndex is not equal to the firstTurnedCardIndex then
                                    // make secondTurnedCardIndex equal to this currentIndex and make cardTwoTurned true...
                                    else if (firstTurnedCardIndex != currentIndex) {
                                        secondTurnedCardIndex = currentIndex;
                                        cardTwoTurned = true;
                                        // This will call the onDraw function to recreate the board based on new conditions...
                                        invalidate();
                                        // As this is the second move so we have to  validate it so call the validateMove function...
                                        validateMove();
                                    }
                                }
                            }

                        }
                    }
                }

                performClick();
                return true;
        }
        return false;
    }

    /**
     * This will validate the move that we have to disappear the cards or make them faced down...
     */
    public void validateMove()
    {
        // First it will display the second card for a second then apply the logic to make both cards disappear or make them again face down...
        new CountDownTimer(1000, 1000) {
            //40000 milli seconds is total time, 1000 milli seconds is time interval

            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                // get value from randomIndices of firstTurnedCardIndex and secondTurnedCardIndex. If they are equal it means that
                // the match has been occured then do the following things...
                if(randomIndices.get(firstTurnedCardIndex) == randomIndices.get(secondTurnedCardIndex))
                {
                    // Add both indices to matched array to keep the trace to matched pairs...
                    matched.add(firstTurnedCardIndex);
                    matched.add(secondTurnedCardIndex);

                    // If turn is one then add one to the player1's score else add one to the player2's score...
                    if(turn == 1)
                        playerOneScore++;
                    else
                        playerTwoScore++;

                    // After a successful match check if the victory condition have been reached or not...
                    checkVictory();
                }

                // Reset the values to keep trace of the next match...
                firstTurnedCardIndex = -1;
                secondTurnedCardIndex = -1;
                cardOneTurned = false;
                cardTwoTurned = false;

                // Also change the turn of current player...
                if(turn == 1)
                    turn = 2;
                else
                    turn = 1;

                // This will update the current game's condition to main activity...
                updateMainActivityToShowScore();
                // Call again the onDraw function to display the new changes...
                invalidate();
            }
        }.start();
    }

    /**
     * This is simple method. it just checks if the win condition is reached. For that we need to have size of matched array
     * eqaul to 16...
     * @returns true if the victory condition is reached else return false...
     */
    public boolean checkVictory()
    {
        if(matched.size() == 16)
        {
            // If size of matched array has been reached to 16 then find the who winner is and display a message using message
            // dialog builder...
            String displayWinner = "Score - [Player 1: " + playerOneScore + "][Player 2: " + playerTwoScore+"]\n\n";
            if (playerOneScore > playerTwoScore)
                displayWinner += "Player one is the winner";
            else if (playerTwoScore > playerOneScore)
                displayWinner += "Player two is the winner";
            else
                displayWinner += "Match is drawn";

            // A AlertDialog to display the final result. and go to start activity by pressing OK...
            new AlertDialog.Builder(getContext())
                    .setTitle("MAN OF THE MATCH")
                    .setMessage(displayWinner)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getContext(),StartActivity.class);
                            getContext().startActivity(i);
                        }
                    }).setNegativeButton("", null).show();

            return true;
        }

        return false;
    }

    /**
     * This method makes a score and a player string and call and update the Main Activity about the score and current player...
     */
    public void updateMainActivityToShowScore()
    {
        MainActivity ma = (MainActivity) this.getContext();
        String score = "Score - [Player 1: " + playerOneScore + "][Player 2: " + playerTwoScore+"]";
        String player = "Current Player: " + turn;
        // Call the main activity function to change the layout...
        ma.updateMainActivity(score,player);
    }
}