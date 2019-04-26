package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.ipsc.repository.WeaponRepository;


@Service
public class WeaponService {
@Autowired
    private WeaponRepository weaponRepository;
}
