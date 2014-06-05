/*
 * Copyright (c) 2012-2014 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

package javaplow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Contractor Class
 * The contractor class is used to create a standard list of assertions that abide by the
 *  snowplow conventions. This ensures that in the following steps of snowplow data processing
 *  all information needed will be present.
 *
 * Contractor Class, mostly used to assert preconditions
 *  Contractor to verify preconditions, post conditions and invariants.
 *  Uses data structure to hold custom ID contracts for specific jobs
 *
 * @version 0.0.2
 * @author Kevin Gleason
 */

public class PlowContractor<T> {
    //Class variables
    private static final String[] SUPPORTED_PLATFORMS = {"pc", "tv", "mob", "cnsl", "iot"};

    //Instance Variables
    private Map<String, Function<T>> customContracts = new HashMap<String, Function<T>>();

    /**
     * Contract Checker
     *   Checks the preconditions and post conditions depending on how it is configured
     *   Can be enabled or disabled as needed with first argument which will be passed by
     *   the Tracker instance.
     * @param checkContracts boolean function passed from Tracker instance, if false contracts
     *                       are turned off and all checks return true.
     * @param function The name of the function to check. Currently three are implemented
     *                 and custom functions are allowed.
     * @param input The input for the function being called and checked if it abides by the
     *              contract.
     * @return True or False depending on whether or not the input abides by the rules of the contract.
     */
    public boolean checkContract(boolean checkContracts, Function<T> function, T input){
        boolean goodContract = checkContracts && function.functionCheck(input) || !checkContracts;
        if (!goodContract)
            throw new Error(function.getErrorMsg());
        return true;
    }

    public void customContract(String name, Function<T> function){
        this.customContracts.put(name, function);
    }

    public boolean checkCustomContract(boolean checkContracts, String name, T input){
        Function<T> function = this.customContracts.get(name);
        boolean goodContract = checkContracts && function.functionCheck(input) || !checkContracts;
        if (!goodContract)
            throw new Error(function.getErrorMsg());
        return true;
    }


    //Interfaces to work around lack of lambda in java 7
    public interface Function<T> {
        public boolean functionCheck(T input);
        public String getErrorMsg();
    }


    //Throw exceptions later
    public static final Function<String> non_empty_string = new Function<String>() {
        public boolean functionCheck(String input){
            return !input.isEmpty() && input.length() > 0;
        }
        public String getErrorMsg(){
            return "String Error - Field cannot be empty string.";
        }
    };

    public static final Function<String> is_supported_platform = new Function<String>(){
        public boolean functionCheck(String input){
            for (String i : SUPPORTED_PLATFORMS)
                if (input.equals(i))
                    return true;
            return false;
        }
        public String getErrorMsg(){
            return "Platform Error - Platform is not supported.";
        }
    };

    public static final Function<String> non_empty_dict = new Function<String>() {
        public boolean functionCheck(String input) {
            return !input.equals("{}") && input.length() > 2;
        }
        public String getErrorMsg() {
            return "Dictionary Error - Cannot use empty dictionary as input.";
        }
    };

    public static final Function<Integer> positive_number = new Function<Integer>() {
        public boolean functionCheck(Integer input){
            return input > 0;
        }
        public String getErrorMsg(){
            return "Integer Error - Field cannot be negative number.";
        }
    };

    public static void main(String[] args){
        //Test cases - contracts enables or disables all contracts
        boolean contracts = false;
        PlowContractor<String> stringContractor = new PlowContractor<String>();
        PlowContractor<Integer> integerContractor = new PlowContractor<Integer>();

        //Able to make custom contracts like so
        PlowContractor.Function<String> string_is_long = new PlowContractor.Function<String>() {
            public boolean functionCheck(String input){
                return !input.isEmpty() && input.length() > 10;
            }
            public String getErrorMsg(){
                return "String Error - Input cannot be a string under length 10.";
            }
        };

        //Check with checkCustomContract
        stringContractor.customContract("dict", string_is_long);
        assert stringContractor.checkCustomContract(contracts,"dict","Hello I Am Kevin");
        assert integerContractor.checkContract(contracts, PlowContractor.positive_number, -10);
        System.out.println((System.currentTimeMillis()/10));
        Date date = new Date(System.currentTimeMillis());
        System.out.println(date.toString());
    }
}
