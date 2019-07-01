<template>
    <el-row>
        <el-col :span="4">
            <el-card :style="cardStyle">
                <div slot="header">
                    <span>直播列表</span>
                    <el-button icon="el-icon-refresh" type="text" class="shotBtn" @click="getVideoList"></el-button>
                </div>
                <el-menu v-if="videos.length > 0">
                    <el-menu-item v-for="video in videos" :key="video.id">
                        <i class="el-icon-video-play"></i>
                        <el-link :underline="false" slot="title" @click="changeVideoUrl(video.url, video.name, video.id)">{{ video.name }}</el-link>
                    </el-menu-item>
                </el-menu>
                <span v-else>暂无直播信息</span>
            </el-card>
        </el-col>
        <el-col :span="20" class="videoRegion">
            <el-card :style="cardStyle" style="text-align: center;">
                <div slot="header">
                    <span>{{ onPlayTitle }}</span>
                    <el-button class="shotBtn" round @click="videoShot">截 图</el-button>
                </div>
                <video id='my-video' controls autoplay :style="videoStyle">
                    <p>Your browser does not support the video tag.</p>
                </video>
            </el-card>
            <el-dialog :visible.sync="shotImgDialogVisible" width="50%">
                <el-input slot="title" type="text" placeholder="请输入截图名称" v-model="shotImgName" maxlength="20" show-word-limit style="width: 50%;"></el-input>
                <el-image :src="shotUrl" :fit="'contain'" style="margin: 0 auto;"></el-image>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="shotImgDialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="uploadShotImg">保 存</el-button>
                </span>
            </el-dialog>
        </el-col>
    </el-row>
</template>

<script>
    import flvjs from 'flv.js';

    export default {
        name: 'videoLive',
        data() {
            return {
                videos: [],
                onPlayTitle: '暂无',
                onPlayId:'',
                videoStyle: {
                    width: screen.availWidth * 0.76 + "px",
                    height: screen.availHeight * 0.75 + "px"
                },
                cardStyle: {
                    height: screen.availHeight * 0.86 + "px"
                },
                shotImgDialogVisible: false,
                shotUrl: '',
                shotImgName: '',
                dssUrl: 'https://whnikoyo.xyz:8042',
                shotCount : 0,
                shotBlob : null
            };
        },
        created() {
            this.getVideoList();
            setInterval(this.getVideoList, 30000);
        },
        methods: {
            changeVideoUrl(videoUrl, videoName, videoId) {
                let player = flvjs.createPlayer({
                    isLive: true,
                    type: 'flv',
                    url: videoUrl
                });
                player.attachMediaElement(document.getElementById('my-video'));
                player.load();
                player.play();

                this.onPlayTitle = videoName;
                this.shotCount = 0;
                this.onPlayId = videoId;
                this.$axios.post(location.origin.concat('/api/liveRecord'),{videoId:videoId,videoName:videoName}).then(resp => console.log(resp)).catch(err => console.log(err));
            },
            getVideoList() {
                let base = location.origin;
                let url = base + '/api/live/sessions';
                let _dssUrl = this.dssUrl;
                this.$axios.post(url).then(resp => {
                    if (resp.data.length > 0) {
                        this.videos = resp.data.map(v => {
                            return {
                                id: v.Id,
                                name: v.Name,
                                url: _dssUrl + v['HTTP-FLV']
                            }
                        });
                    } else {
                        this.videos = [];
                    }
                }).catch(err => {
                    console.log(err);
                    this.$message.error('获取直播列表失败');
                })
            },
            videoShot() {
                let s_video = document.getElementById('my-video');
                if(s_video.readyState == 0) {
                    this.$message.error('暂无直播信息，截图失败');
                    return;
                }
                let s_canvas = document.createElement("canvas");
                s_canvas.width = s_video.videoWidth;
                s_canvas.height = s_video.videoHeight;
                s_canvas.getContext('2d').drawImage(s_video, 0, 0, s_canvas.width, s_canvas.height);
                this.shotUrl = s_canvas.toDataURL();
                this.shotImgDialogVisible = true;
                this.shotImgName = this.onPlayTitle+'_'+this.shotCount;
                this.shotCount ++;
                s_canvas.toBlob(b => this.shotBlob = b);
            },
            uploadShotImg() {
                let base = location.origin;
                let config = {
                    headers: { "Content-Type": "multipart/form-data" }
                };
                this.shotBlob.lastModifiedDate = new Date();
                this.shotBlob.name = this.shotImgName+'.png';
                let form = new FormData();
                form.append('shotImg', this.shotBlob, this.shotBlob.name);
                this.$axios.post(base.concat('/api/shotImg/',this.onPlayId), form, config)
                    .then(resp => {
                        if(resp.data == 'success') {
                            this.$message({
                                message: this.shotImgName + '保存成功！',
                                type: 'success'
                            });
                            this.shotImgDialogVisible = false;
                        } else {
                            this.$message.error(this.shotImgName + '保存失败！');
                        }
                    }).catch(err => {
                        this.$message.error(this.shotImgName + '保存失败！');
                        console.log(err)
                    }).finally(() => {
                        this.shotBlob = null;
                    });
            }
        }
    }
</script>

<style scoped>
    h3 {
        text-align: center;
    }

    .videoRegion {
        padding-left: 10px;
    }

    .shotBtn {
        float: right;
        position: relative;
        top: -0.5rem;
    }
</style>
