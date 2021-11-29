package com.example.momentum;

import com.example.momentum.sharing.FollowingEntity;

import junit.framework.TestCase;

public class FollowingEntityTest extends TestCase {
    String name = "boxiao";
    String uid = "boxiao12345";
    String set_name = "krystal";
    String set_uid = "kyrstal123";

    FollowingEntity followingEntity = new FollowingEntity(name,uid);


    public void testTestGetName() {
        assertEquals(name,followingEntity.getName());
    }

    public void testTestSetName() {
        followingEntity.setName("krystal");
        assertEquals(set_name,followingEntity.getName());

    }

    public void testGetUid() {
        assertEquals(uid,followingEntity.getUid());
    }

    public void testSetUid() {
        followingEntity.setUid("kyrstal123");
        assertEquals(set_uid,followingEntity.getUid());
    }
}
