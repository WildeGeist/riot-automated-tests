package mobilestests_android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.esotericsoftware.yamlbeans.YamlException;

import pom_android.RiotRoomPageObjects;
import pom_android.main_tabs.RiotHomePageTabObjects;
import utility.Constant;
import utility.HttpsRequestsToMatrix;
import utility.ReadConfigFile;
import utility.RiotParentTest;
import utility.ScreenshotUtility;

@Listeners({ ScreenshotUtility.class })
public class RiotRoomInvitationTests extends RiotParentTest{
	private String roomId="!WnWDyMPdDkzMLHuGXK%3Amatrix.org";
	private String roomIdCustomHs="!AoVlZJKRJGotpMJqYg%3Ajeangb.org";
	
	String riotUserDisplayName="riotuser16";
	String riotInviterUserDisplayName="riotuserup";
	String riotInviterAccessToken;
	String roomName="invitation auto tests";
	
	/**
	 * Required : the test user hasn't received any invitation. </br>
	 * Receive an invitation to a room. </br>
	 * Check that riot allows the user to accept or decline the invitation.</br>
	 * Check that the invitation is closed when accepted.</br>
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test(groups={"1driver_android","1checkuser"})
	public void rejectInvitationToARoom() throws IOException, InterruptedException{
		HttpsRequestsToMatrix.kickUser(riotInviterAccessToken, getRoomId(), getMatrixIdFromDisplayName(riotUserDisplayName));
		Thread.sleep(1000);
		HttpsRequestsToMatrix.sendInvitationToUser(riotInviterAccessToken, getRoomId(), getMatrixIdFromDisplayName(riotUserDisplayName));
		
		RiotHomePageTabObjects homePage=new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		ExplicitWait(appiumFactory.getAndroidDriver1(),homePage.invitesSectionLayout);
		Assert.assertTrue(homePage.invitesSectionLayout.isDisplayed(), "The invites collapsing bar isn't displayed");
		//TODO check that invites layout is above rooms list
		//check invite layout
		homePage.checkInvitationLayout(roomName);
		//reject invitation
		homePage.rejectInvitation(roomName);
		//check that the invite bar is closed
		Assert.assertFalse(waitUntilDisplayed(appiumFactory.getAndroidDriver1(),"//android.widget.TextView[@resource-id='im.vector.alpha:id/heading' and @text='INVITES']/../..", false, 5),"The INVITES bar isn't closed after rejecting the invitation");
	}
	
	/**
	 * Required : the test user hasn't received any invitation. </br>
	 * Receive an invitation to a room. </br>
	 * Check that riot allows the user to accept or decline the invitation.</br>
	 * Click preview to preview the room.</br>
	 * Check the preview layout.</br>
	 * Cancel the invitation.</br>
	 * Check that the invitation is closed.
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test(groups={"1driver_android","1checkuser"})
	public void cancelInvitationToARoom() throws IOException, InterruptedException{
		HttpsRequestsToMatrix.kickUser(riotInviterAccessToken, getRoomId(), getMatrixIdFromDisplayName(riotUserDisplayName));
		Thread.sleep(1000);
		HttpsRequestsToMatrix.sendInvitationToUser(riotInviterAccessToken, getRoomId(), getMatrixIdFromDisplayName(riotUserDisplayName));
		
		RiotHomePageTabObjects homePage=new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		ExplicitWait(appiumFactory.getAndroidDriver1(),homePage.invitesSectionLayout);
		Assert.assertTrue(homePage.invitesSectionLayout.isDisplayed(), "The invites collapsing bar isn't displayed");
		//TODO check that invites layout is above rooms list
		//check invite layout
		homePage.checkInvitationLayout(roomName);
		//preview invitation
		homePage.previewInvitation(roomName);
		//check the preview layout
		RiotRoomPageObjects newRoom = new RiotRoomPageObjects(appiumFactory.getAndroidDriver1());
		newRoom.checkPreviewRoomLayout(roomName);
		//cancel invitation
		newRoom.cancelInvitationButton.click();
		homePage = new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		//check that the invite bar is closed
		Assert.assertFalse(waitUntilDisplayed(appiumFactory.getAndroidDriver1(),"//android.widget.TextView[@resource-id='im.vector.alpha:id/heading' and @text='INVITES']/../..", false, 5),"The INVITES bar isn't closed after rejecting the invitation");
	}

	/**
	 * Required : the test user hasn't received any invitation. </br>
	 * Receive an invitation to a room. </br>
	 * Check that riot allows the user to accept or decline the invitation.</br>
	 * Click preview to preview the room.</br>
	 * Check the preview layout.</br>
	 * Join the room</br>
	 * Check that the room is opened. </br>
	 * Came back in the list and leave room.</br>
	 * Check that room page is closed and not present in the rooms list.
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test(groups={"1driver_android","1checkuser"})
	public void acceptInvitationToARoom() throws IOException, InterruptedException{
		HttpsRequestsToMatrix.kickUser(riotInviterAccessToken, getRoomId(), getMatrixIdFromDisplayName(riotUserDisplayName));
		Thread.sleep(1000);
		HttpsRequestsToMatrix.sendInvitationToUser(riotInviterAccessToken, getRoomId(), getMatrixIdFromDisplayName(riotUserDisplayName));
		
		RiotHomePageTabObjects homePage=new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		ExplicitWait(appiumFactory.getAndroidDriver1(),homePage.invitesSectionLayout);
		Assert.assertTrue(homePage.invitesSectionLayout.isDisplayed(), "The invites collapsing bar isn't displayed");
		//TODO check that invites layout is above rooms list
		//check invite layout
		homePage.checkInvitationLayout(roomName);
		//preview invitation
		homePage.previewInvitation(roomName);
		//check the preview layout
		RiotRoomPageObjects newRoom = new RiotRoomPageObjects(appiumFactory.getAndroidDriver1());
		newRoom.checkPreviewRoomLayout(roomName);
		newRoom.joinRoomButton.click();
		newRoom = new RiotRoomPageObjects(appiumFactory.getAndroidDriver1());
		newRoom.checkRoomLayout(roomName);
		//come back in roomslist
		newRoom.menuBackButton.click();
		homePage = new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		//leave the room
		homePage.leaveRoom(roomName);
		//check that room is closed and isn't in the rooms list page
		newRoom.isDisplayed(false);
		homePage = new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		Assert.assertNull(homePage.getRoomByName(roomName), "Room "+roomName+" is still in the rooms list after leaving it.");
	}
	
	/**
	 * Required : the test user hasn't received any invitation. </br>
	 * Receive an invitation to a room. </br>
	 * Check that riot allows the user to accept or decline the invitation.</br>
	 * Click preview to preview the room.</br>
	 * Check the preview layout.</br>
	 * Join the room</br>
	 * Check that the room is opened. </br>
	 * Came back in the list and leave room from the room menu.</br>
	 * Check that room page is closed and not present in the rooms list.
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	@Test(groups={"1driver_android","1checkuser"})
	public void acceptInvitationAndLeaveFromMenu() throws IOException, InterruptedException{
		HttpsRequestsToMatrix.kickUser(riotInviterAccessToken, getRoomId(), getMatrixIdFromDisplayName(riotUserDisplayName));
		Thread.sleep(1000);
		HttpsRequestsToMatrix.sendInvitationToUser(riotInviterAccessToken, getRoomId(), getMatrixIdFromDisplayName(riotUserDisplayName));
		
		RiotHomePageTabObjects homePage=new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		ExplicitWait(appiumFactory.getAndroidDriver1(),homePage.invitesSectionLayout);
		Assert.assertTrue(homePage.invitesSectionLayout.isDisplayed(), "The invites collapsing bar isn't displayed");
		//TODO check that invites layout is above rooms list
		//check invite layout
		homePage.checkInvitationLayout(roomName);
		//preview invitation
		homePage.previewInvitation(roomName);
		//check the preview layout
		RiotRoomPageObjects newRoom = new RiotRoomPageObjects(appiumFactory.getAndroidDriver1());
		newRoom.checkPreviewRoomLayout(roomName);
		newRoom.joinRoomButton.click();
		newRoom = new RiotRoomPageObjects(appiumFactory.getAndroidDriver1());
		newRoom.checkRoomLayout(roomName);
		//leave room from room menu
		newRoom.leaveRoom();
		//check that room is closed and isn't in the rooms list page
		newRoom.isDisplayed(false);
		homePage = new RiotHomePageTabObjects(appiumFactory.getAndroidDriver1());
		Assert.assertNull(homePage.getRoomByName(roomName), "Room "+roomName+" is still in the rooms list after leaving it.");
	}
	
	/**
	 * Stress test on invitations.
	 * Receive multiple (10) invitations from an other user. </br>
	 * TODO write this test
	 */
	@Test(enabled=false)
	public void receiveMultipleInvitations(){
		
	}
	/**
	 * TODO write this test
	 */
	@Test(enabled=false)
	public void sendInvitationOnRestrictedRoom(){
		
	}
	
	/**
	 * TODO write this test
	 */
	@Test(enabled=false)
	public void leaveRoom(){
		
	}

	private String getRoomId() {
		try {
			if("false".equals(ReadConfigFile.getInstance().getConfMap().get("homeserverlocal"))){
				return roomId;
			}else{
				return roomIdCustomHs;
			}
		} catch (FileNotFoundException | YamlException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Log the good user if not.</br> Secure the test.
	 * @param myDriver
	 * @param username
	 * @param pwd
	 * @throws InterruptedException 
	 * @throws YamlException 
	 * @throws FileNotFoundException 
	 */
	@BeforeGroups("1checkuser")
	private void checkIfUserLogged() throws InterruptedException, FileNotFoundException, YamlException{
		super.checkIfUserLoggedAndHomeServerSetUpAndroid(appiumFactory.getAndroidDriver1(), riotUserDisplayName, Constant.DEFAULT_USERPWD);
	}
	
	/**
	 * Log riotuserup to get his access token. </br> Mandatory to send http request with it.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@BeforeGroups("1checkuser")
	private void renewRiotInviterAccessToken() throws IOException, InterruptedException{
		System.out.println("Log "+riotInviterUserDisplayName+" to get a new AccessToken.");
		riotInviterAccessToken=HttpsRequestsToMatrix.login(riotInviterUserDisplayName, Constant.DEFAULT_USERPWD);
	}
	
	@AfterMethod(alwaysRun=true,groups={"1driver_android"})
	private void restart1ApplicationAfterTest(Method m) throws InterruptedException{
		restartApplication(appiumFactory.getAndroidDriver1());
	}
}
