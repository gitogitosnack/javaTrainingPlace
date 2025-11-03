package calc_app.main;

import calc_app.calcLogics.CalcLogic;

public class MainCalc{
    public static void main(String[] args) {
        System.out.println("Start: multimethods calling from now on.");
        
        // define a and b.
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);

        // call the results.
        int resultOfPlus = CalcLogic.plusCalc(a, b);
        int resultOfMinus = CalcLogic.minusCalc(a, b);

        // display the results.
        System.out.println("**********************************************************");
        System.out.println("The Plus Answer is " + resultOfPlus + ". ");
        System.out.println("The Minus Answer is " + resultOfMinus + ". ");
        System.out.println("**********************************************************");

        System.out.println("End: multimethods ended");

    } 
}