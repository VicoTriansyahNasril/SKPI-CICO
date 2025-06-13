package com.skpijtk.springboot_boilerplate.repository;

import com.skpijtk.springboot_boilerplate.model.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppSettingRepository extends JpaRepository<AppSetting, String> {
}
