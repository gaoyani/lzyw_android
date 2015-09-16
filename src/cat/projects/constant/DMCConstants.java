package cat.projects.constant;

public class DMCConstants {


	private static final int MEDIA_DMC_CTL_MSG_BASE = 0x100;
	/*----------------------------------------------------------------*/
	public static final int MEDIA_DMC_CTL_MSG_SET_AV_URL = (MEDIA_DMC_CTL_MSG_BASE+0);
	public static final int MEDIA_DMC_CTL_MSG_STOP = (MEDIA_DMC_CTL_MSG_BASE+1);
	public static final int MEDIA_DMC_CTL_MSG_PLAY = (MEDIA_DMC_CTL_MSG_BASE+2);
	public static final int MEDIA_DMC_CTL_MSG_PAUSE = (MEDIA_DMC_CTL_MSG_BASE+3);
	public static final int MEDIA_DMC_CTL_MSG_SEEK = (MEDIA_DMC_CTL_MSG_BASE+4);
	public static final int MEDIA_DMC_CTL_MSG_SETVOLUME = (MEDIA_DMC_CTL_MSG_BASE+5);
	public static final int MEDIA_DMC_CTL_MSG_SETMUTE = (MEDIA_DMC_CTL_MSG_BASE+6);
	public static final int MEDIA_DMC_CTL_MSG_SETPLAYMODE = (MEDIA_DMC_CTL_MSG_BASE+7);
	public static final int MEDIA_DMC_CTL_MSG_PRE = (MEDIA_DMC_CTL_MSG_BASE+8);
	public static final int MEDIA_DMC_CTL_MSG_NEXT = (MEDIA_DMC_CTL_MSG_BASE+9);
	public static final int MEDIA_DMC_CTL_MSG_GETLIST = (MEDIA_DMC_CTL_MSG_BASE+10);
	public static final int MEDIA_DMC_CTL_MSG_TOTALTIME = (MEDIA_DMC_CTL_MSG_BASE+11);	
	public static final int MEDIA_DMC_CTL_MSG_CURRENTTIME = (MEDIA_DMC_CTL_MSG_BASE+12);	
	public static final int MEDIA_DMC_CTL_MSG_DMRLOST = (MEDIA_DMC_CTL_MSG_BASE+13);	
	public static final int MEDIA_DMC_CTL_STOP = (MEDIA_DMC_CTL_MSG_BASE+14);
	/*----------------------------------------------------------------*/

    public static final class Action{
        public static final String ACTION_PUSH_FILE_TV="intent.push.file.tv";
    }

}
