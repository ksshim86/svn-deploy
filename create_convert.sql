
    create table app_pr (
       ordr integer not null,
        pjt_no integer not null,
        created_dt datetime(6) not null,
        updated_dt datetime(6) not null,
        del_yn varchar(1) default 'N' not null,
        use_yn CHAR(1) default 'N' default 'N' not null,
        role_cd varchar(255) not null,
        primary key (pjt_no, ordr)
    ) engine=InnoDB;

    create table deploy (
       dp_no integer not null auto_increment,
        created_dt datetime(6) not null,
        updated_dt datetime(6) not null,
        del_yn varchar(1) default 'N' not null,
        deadline_dt datetime(6) not null,
        dp_div CHAR(1) not null,
        dp_dt datetime(6) not null,
        dp_st CHAR(1) default 'I' not null,
        dp_title varchar(255) not null,
        pjt_no integer not null,
        primary key (dp_no)
    ) engine=InnoDB;

    create table project (
       pjt_no integer not null auto_increment,
        created_dt datetime(6) not null,
        updated_dt datetime(6) not null,
        del_yn varchar(1) default 'N' not null,
        dcr bigint not null,
        dev_svn_url varchar(255) not null,
        dp_st CHAR(1) default 'N' not null,
        dp_svn_url varchar(255) not null,
        pjt_key varchar(255) not null,
        pjt_nm varchar(255) not null,
        rcs_st CHAR(1) default 'N' not null,
        started_yn CHAR(1) default 'N' not null,
        svn_password varchar(255) not null,
        svn_username varchar(255) not null,
        primary key (pjt_no)
    ) engine=InnoDB;

    create table role (
       role_cd varchar(255) not null,
        role_lvl integer not null,
        role_nm varchar(255) not null,
        primary key (role_cd)
    ) engine=InnoDB;

    create table sd_info (
       sd_info_no integer not null auto_increment,
        created_dt datetime(6) not null,
        updated_dt datetime(6) not null,
        sd_root_path varchar(255) not null,
        primary key (sd_info_no)
    ) engine=InnoDB;

    create table sd_path (
       ordr integer not null,
        pjt_no integer not null,
        rev_no integer not null,
        action CHAR(1) not null,
        combined varchar(255) not null,
        copy_file_nm varchar(255),
        copy_file_path varchar(255),
        copy_rev_no integer,
        file_nm VARCHAR(1000) not null,
        file_path TEXT not null,
        kind CHAR(1) not null,
        sub_pjt_no integer,
        primary key (pjt_no, rev_no desc, ordr desc)
    ) engine=InnoDB;

    create table sd_revision (
       pjt_no integer not null,
        rev_no integer not null,
        author varchar(255) not null,
        msg TEXT,
        rev_dt datetime(6) not null,
        primary key (pjt_no, rev_no desc)
    ) engine=InnoDB;

    create table sub_project (
       pjt_no integer not null,
        sub_pjt_no integer not null,
        created_dt datetime(6) not null,
        updated_dt datetime(6) not null,
        sub_pjt_nm varchar(255) not null,
        primary key (pjt_no, sub_pjt_no)
    ) engine=InnoDB;

    create table team (
       team_no integer not null auto_increment,
        created_dt datetime(6) not null,
        updated_dt datetime(6) not null,
        del_yn varchar(1) default 'N' not null,
        team_nm varchar(255) not null,
        primary key (team_no)
    ) engine=InnoDB;

    create table user (
       user_id varchar(255) not null,
        created_dt datetime(6) not null,
        updated_dt datetime(6) not null,
        del_yn varchar(1) default 'N' not null,
        password varchar(255),
        user_nm varchar(255),
        team_no integer,
        primary key (user_id)
    ) engine=InnoDB;

    create table user_role (
    	user_id varchar(255) not null,
       role_cd varchar(255) not null,
        user_role_no integer,
        primary key (user_id, role_cd)
    ) engine=InnoDB;

    alter table project 
       add constraint UK_xu98b0ag0ww1s0beye395bnl unique (pjt_key);
    
    alter table sub_project 
       add constraint unique_idex_sub_project_subPjtNm unique (sub_pjt_nm);

    alter table app_pr 
       add constraint FK8cn4fkeqtfjl6vk4dun4ad0on 
       foreign key (pjt_no) 
       references project (pjt_no);

    alter table app_pr 
       add constraint FKd9wbcgmbhsnnuojwngr01htqp 
       foreign key (role_cd) 
       references role (role_cd);

    alter table deploy 
       add constraint FK2bqpm9s24tpffermy6ex4f30j 
       foreign key (pjt_no) 
       references project (pjt_no);

    alter table sd_path 
       add constraint FKiruo2utw2s3hla9dkbqi19o12 
       foreign key (pjt_no, rev_no) 
       references sd_revision (pjt_no, rev_no);

    alter table sd_path 
       add constraint FKh7svn96p8c3k6x2e61guc61i 
       foreign key (pjt_no, sub_pjt_no) 
       references sub_project (pjt_no, sub_pjt_no);

    alter table sd_revision 
       add constraint FKhjpgx0syhkmeyvyscgc88i7vs 
       foreign key (pjt_no) 
       references project (pjt_no);

    alter table sub_project 
       add constraint FKgq7xf4xt3lbolay0gwnto1y3c 
       foreign key (pjt_no) 
       references project (pjt_no);

    alter table user 
       add constraint FK5npf04k4w955xmrh06bound7o 
       foreign key (team_no) 
       references team (team_no);

    alter table user_role 
       add constraint FK9x6fh31feadsw5ex9v5nn94h5 
       foreign key (role_cd) 
       references role (role_cd);

    alter table user_role 
       add constraint FK859n2jvi8ivhui0rl0esws6o 
       foreign key (user_id) 
       references user (user_id);
