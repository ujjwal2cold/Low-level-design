package Strategies;

import Models.Member;

import java.util.List;

public interface ISplittingStrategy {
   List<Member> splitAmongUsers();
}
