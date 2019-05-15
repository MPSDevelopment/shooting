package tech.shooting.ipsc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.shooting.commons.exception.BadRequestException;
import tech.shooting.commons.pojo.ErrorMessage;
import tech.shooting.ipsc.bean.WeaponBean;
import tech.shooting.ipsc.pojo.Division;
import tech.shooting.ipsc.pojo.Person;
import tech.shooting.ipsc.pojo.Weapon;
import tech.shooting.ipsc.pojo.WeaponType;
import tech.shooting.ipsc.repository.DivisionRepository;
import tech.shooting.ipsc.repository.PersonRepository;
import tech.shooting.ipsc.repository.WeaponRepository;
import tech.shooting.ipsc.repository.WeaponTypeRepository;

import java.util.List;


@Service
public class WeaponService {
    @Autowired
    private WeaponRepository weaponRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private DivisionRepository divisionRepository;

    @Autowired
    private WeaponTypeRepository typeRepository;

    public List<Weapon> getAll() {
        return weaponRepository.findAll();
    }

    public Weapon getWeaponById(Long weaponId) throws BadRequestException {
        return checkWeapon(weaponId);
    }

    private Weapon checkWeapon(Long weaponId) throws BadRequestException {
        return weaponRepository.findById(weaponId).orElseThrow(()-> new BadRequestException(new ErrorMessage("Weapon with this id %s is not exist",weaponId)));
    }

    public Weapon postWeapon(WeaponBean bean) throws BadRequestException {
        Person owner = checkPerson(bean.getOwner());
        Division division = checkDivision(bean.getDivision());
        Weapon bySerialNumber = weaponRepository.findBySerialNumber(bean.getSerialNumber());
        WeaponType weaponType = checkWeaponType(bean.getWeaponType());
        if (bySerialNumber == null){
            bySerialNumber = new Weapon().setDivision(division).setOwner(owner).setSerialNumber(bean.getSerialNumber()).setCount(bean.getCount()).setWeaponName(weaponType);
        }else{
            bySerialNumber.setDivision(division).setOwner(owner).setSerialNumber(bean.getSerialNumber()).setCount(bean.getCount()).setWeaponName(weaponType);
        }
        return weaponRepository.save(bySerialNumber);
    }

    private WeaponType checkWeaponType(Long weaponType) throws BadRequestException {
        return typeRepository.findById(weaponType).orElseThrow(()-> new BadRequestException(new ErrorMessage("Incorrect weapon type id %s",weaponType)));
    }

    private Division checkDivision(Long division) throws BadRequestException {
        return divisionRepository.findById(division).orElseThrow(()-> new BadRequestException(new ErrorMessage("Division with id %s is not exist",division)));
    }

    private Person checkPerson(Long owner) throws BadRequestException {
        return personRepository.findById(owner).orElseThrow(()-> new BadRequestException(new ErrorMessage("Person with id %s is not exist",owner)));
    }

    public void deleteWeapon(long weaponId) {
        weaponRepository.deleteById(weaponId);
    }

    public Weapon addOwnerToWeapon(Long weaponId, Long personId) throws BadRequestException {
        Weapon weapon = checkWeapon(weaponId);
        if (personId == null) {
            weapon.setOwner(null);
        } else {
            weapon.setOwner(checkPerson(personId));
        }
        return weapon;
    }

    public Weapon addNumberOfShootingForWeapon(Long weaponId, Integer firedCount) throws BadRequestException {
        Weapon weapon = checkWeapon(weaponId);
        checkNumberShootings(weapon.getCount(),firedCount);
        return weaponRepository.save(weapon.setCount(firedCount));
    }

    private void checkNumberShootings(Integer count, Integer firedCount) throws BadRequestException {
        if (count > firedCount){
           throw  new BadRequestException(new ErrorMessage("You try set incorrect data fired count "+firedCount+" must be more than previous count "));
        }
    }

    public List<Weapon> getAllByDivision(Long divisionId) throws BadRequestException {
        return weaponRepository.findAllByDivision(checkDivision(divisionId));
    }

    public List<Weapon> getAllByPerson(Long personId) throws BadRequestException {
            return weaponRepository.findAllByOwner(checkPerson(personId));
    }
}
