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
        candidates.add(new Candidate("Donald", "Trump", "Republican Party"));
        candidates.add(new Candidate("Chris", "Keniston", "Veterans Party"));
        candidates.add(new Candidate("Jill", "Stein", "Green Party"));
        candidates.add(new Candidate("Gary", "Johnson", "Libertarian Party"));
        candidates.add(new Candidate("Darrell", "Castle", "Constitution Party"));
        candidates.add(new Candidate("Hillary", "Clinton", "Democratic Party"));
    }

    public List<Candidate> getCandidates() {
        candidates.sort(Comparator.comparing(Candidate::getLastName));
        return candidates;
    }
}
