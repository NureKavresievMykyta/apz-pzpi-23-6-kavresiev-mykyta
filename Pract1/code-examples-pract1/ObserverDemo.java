package ua.nure.kavresiev.pz;

import java.util.ArrayList;
import java.util.List;

interface Observer {
    void update(String result);
}

class MedicalResultSubject {
    private final List<Observer> observers = new ArrayList<>();
    private String result;

    public void attach(Observer observer) {
        observers.add(observer);
    }

    public void detach(Observer observer) {
        observers.remove(observer);
    }

    public void setResult(String result) {
        this.result = result;
        notifyObservers();
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(result);
        }
    }
}

class DoctorPanel implements Observer {
    @Override
    public void update(String result) {
        System.out.println("Панель лікаря оновлено. Новий результат: " + result);
    }
}

class PatientNotificationService implements Observer {
    @Override
    public void update(String result) {
        System.out.println("Пацієнту надіслано повідомлення про новий результат.");
    }
}

class AuditLog implements Observer {
    @Override
    public void update(String result) {
        System.out.println("Запис у журналі аудиту: додано результат - " + result);
    }
}

public class ObserverDemo {
    public static void main(String[] args) {
        MedicalResultSubject medicalResult = new MedicalResultSubject();

        Observer doctorPanel = new DoctorPanel();
        Observer notificationService = new PatientNotificationService();
        Observer auditLog = new AuditLog();

        medicalResult.attach(doctorPanel);
        medicalResult.attach(notificationService);
        medicalResult.attach(auditLog);

        medicalResult.setResult("Гострота зору: 0.8");
    }
}