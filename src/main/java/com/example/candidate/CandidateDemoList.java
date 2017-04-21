package com.example.candidate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class CandidateDemoList {

    private List<Candidate> candidates;

    public CandidateDemoList() {
        candidates = new ArrayList<>();
        setCandidates();
    }

    private void setCandidates() {
        candidates.add(new Candidate("Mitt", "Romney", "Republican Party", "2012 Presidential Election"));
        candidates.add(new Candidate("Rocky ", "Anderson", "Justice Party", "2012 Presidential Election"));
        candidates.add(new Candidate("Jill", "Stein", "Green Party", "2012 Presidential Election"));
        candidates.add(new Candidate("Gary", "Johnson", "Libertarian Party", "2012 Presidential Election"));
        candidates.add(new Candidate("Virgil", "Goode", "Constitution Party", "2012 Presidential Election"));
        candidates.add(new Candidate("Barack", "Obama", "Democratic Party", "2012 Presidential Election"));
        candidates.add(new Candidate("Donald", "Trump", "Republican Party", "2016 Presidential Election"));
        candidates.add(new Candidate("Chris", "Keniston", "Veterans Party", "2016 Presidential Election"));
        candidates.add(new Candidate("Jill", "Stein", "Green Party", "2016 Presidential Election"));
        candidates.add(new Candidate("Gary", "Johnson", "Libertarian Party", "2016 Presidential Election"));
        candidates.add(new Candidate("Darrell", "Castle", "Constitution Party", "2016 Presidential Election"));
        candidates.add(new Candidate("Hillary", "Clinton", "Democratic Party", "2016 Presidential Election"));
    }

    public List<Candidate> getCandidates() {
        candidates.sort(Comparator.comparing(Candidate::getLastName));
        return candidates;
    }
}
