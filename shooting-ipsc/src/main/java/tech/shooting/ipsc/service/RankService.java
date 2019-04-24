package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.shooting.ipsc.pojo.Rank;
import tech.shooting.ipsc.repository.RankRepository;
import java.util.List;

@Service
public class RankService {

    @Autowired
    private RankRepository rankRepository;


    public List<Rank> getAll() {
        List<Rank> all = rankRepository.findAll();
        return all;
    }
}
