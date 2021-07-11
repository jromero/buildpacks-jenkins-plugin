package io.jenkins.plugins.lifecycle.api;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ArrayListIsZeroException extends RuntimeException {
    public ArrayListIsZeroException(String message){
        super(message);
    }
}

public class Version {

    public long Major;
    public long Minor;

    Version(){}
    Version(long major, long minor){
        this.Major = major;
        this.Minor = minor;
    }

    public static Version NewVersion(String v) {
        long major = 0, minor = 0;
        try {
            Pattern pattern = Pattern.compile("^v?(\\d+)\\.?(\\d*)$");
            List<String[]> matches = FindAllStringSubmatch(v, -1, pattern);  

            try {

                if (matches.size() == 0) {
                    throw new ArrayListIsZeroException(String.format("could not parse '%s' as version", v));
                }      

                if (matches.get(0).length == 3) {

                    major = Long.parseLong(matches.get(0)[1]);

                    if (matches.get(0)[2] == "") {
                        minor = 0;
                    } else {

                        try {
                            minor = Long.parseLong(matches.get(0)[2]);
                        } catch (NumberFormatException e) {
                            System.out.println(String.format("parsing Minor '%s'", matches.get(0)[2]));  
                        } catch (Exception e) {
                            System.out.println(e.getMessage());  
                        }
        
                    }
                } else {
                    throw new Exception(String.format("could not parse version '%s'", v));
                }
              
            } catch (NumberFormatException e) {
                System.out.println(String.format("parsing Major '%s'", matches.get(0)[1]));  
            } catch (ArrayListIsZeroException e) {
                System.out.println(e.getMessage());  
            } catch (Exception e) {
                System.out.println(e.getMessage());  
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());  
        }

        return new Version(major, minor);
    }

    public static List<String[]> FindAllStringSubmatch(String s, int n, Pattern p) {
        if(n < 0) {
            n = s.length() + 1;
        }
        List<String[]> allMatches = new ArrayList<String[]>();
        Matcher m = p.matcher(s);
        while (m.find()) {
            String[] group = new String[]{m.group(), m.group(1), m.group(2)};
            allMatches.add(group);
        }
        return allMatches;
    }

    public int Compare(Version o) {
        if (this.Major != o.Major) {
            if (this.Major < o.Major) {
                return -1;
            }
    
            if (this.Major > o.Major) {
                return 1;
            }
        }
    
        if (this.Minor != o.Minor) {
            if (this.Minor < o.Minor) {
                return -1;
            }
    
            if (this.Minor > o.Minor) {
                return 1;
            }
        }
    
        return 0;
    }
}
